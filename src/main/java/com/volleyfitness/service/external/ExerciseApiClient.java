package com.volleyfitness.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volleyfitness.service.interfaces.ExerciseService;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
public class ExerciseApiClient implements ExerciseService {
  private static final Logger log = LoggerFactory.getLogger(ExerciseApiClient.class);

  private final WebClient exerciseWebClient;
  private final ObjectMapper objectMapper;
  private final String apiKey;
  private final String apiHost;
  private final String fallbackImage;
  private final Duration timeout;
  private final int retryAttempts;

  public ExerciseApiClient(WebClient exerciseWebClient,
                           ObjectMapper objectMapper,
                           @Value("${app.exercise.api-key}") String apiKey,
                           @Value("${app.exercise.api-host}") String apiHost,
                           @Value("${app.exercise.fallback-image}") String fallbackImage,
                           @Value("${app.exercise.timeout-seconds}") long timeoutSeconds,
                           @Value("${app.exercise.retry-attempts}") int retryAttempts) {
    this.exerciseWebClient = exerciseWebClient;
    this.objectMapper = objectMapper;
    this.apiKey = apiKey;
    this.apiHost = apiHost;
    this.fallbackImage = fallbackImage;
    this.timeout = Duration.ofSeconds(timeoutSeconds);
    this.retryAttempts = retryAttempts;
  }

  @Override
  public Map<String, String> fetchExerciseImages(Iterable<String> names) {
    Map<String, String> images = new HashMap<>();
    images.put("fallback", fallbackImage);

    for (String name : names) {
      if (name == null || name.isBlank()) {
        continue;
      }
      String key = name.toLowerCase();
      try {
        String resolvedImage = null;
        for (String candidate : buildCandidates(name)) {
          String response = exerciseWebClient.get()
            .uri(uriBuilder -> uriBuilder.path("/exercises/name/{name}")
              .queryParam("limit", 1)
              .queryParam("offset", 0)
              .build(candidate))
            .header("X-RapidAPI-Key", apiKey)
            .header("X-RapidAPI-Host", apiHost)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(timeout)
            .retryWhen(Retry.fixedDelay(retryAttempts, Duration.ofSeconds(1)))
            .block();

          if (response == null || response.isBlank()) {
            continue;
          }

          JsonNode root = objectMapper.readTree(response);
          JsonNode gifNode = root.isArray() ? root.path(0).path("gifUrl") : root.at("/results/0/gifUrl");
          if (!gifNode.isMissingNode() && !gifNode.asText().isBlank()) {
            resolvedImage = gifNode.asText();
            break;
          }
        }

        images.put(key, resolvedImage == null ? fallbackImage : resolvedImage);
      } catch (Exception ex) {
        log.warn("Exercise API failed for {}", name, ex);
        images.put(key, fallbackImage);
      }
    }

    return images;
  }

  private Set<String> buildCandidates(String rawName) {
    Set<String> candidates = new LinkedHashSet<>();
    String normalized = rawName.trim().replaceAll("\\s+", " ");
    if (normalized.isBlank()) {
      return candidates;
    }

    candidates.add(normalized);

    String withoutParentheses = normalized.replaceAll("\\([^)]*\\)", "").trim().replaceAll("\\s+", " ");
    if (!withoutParentheses.isBlank()) {
      candidates.add(withoutParentheses);
    }

    int parenStart = normalized.indexOf('(');
    int parenEnd = normalized.indexOf(')');
    if (parenStart >= 0 && parenEnd > parenStart) {
      String inside = normalized.substring(parenStart + 1, parenEnd);
      for (String part : inside.split(",")) {
        String candidate = part.trim().replaceAll("\\s+", " ");
        if (!candidate.isBlank()) {
          candidates.add(candidate);
        }
      }
    }

    String[] commaParts = normalized.split(",");
    for (String part : commaParts) {
      String candidate = part.trim().replaceAll("\\s+", " ");
      if (!candidate.isBlank()) {
        candidates.add(candidate);
      }
    }

    return candidates;
  }
}

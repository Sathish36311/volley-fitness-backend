package com.volleyfitness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volleyfitness.service.external.ExerciseApiClient;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseApiClientTest {
  @Test
  void returnsFallbackOnError() {
    ExchangeFunction exchangeFunction = request -> Mono.error(new RuntimeException("timeout"));
    WebClient webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();

    ExerciseApiClient client = new ExerciseApiClient(webClient, new ObjectMapper(), "key", "fallback", 1, 0);
    Map<String, String> result = client.fetchExerciseImages(List.of("Jump"));

    assertEquals("fallback", result.get("jump"));
  }

  @Test
  void readsGifUrlWhenPresent() {
    String body = "{\"results\":[{\"gifUrl\":\"http://gif\"}]}";

    ExchangeFunction exchangeFunction = request -> Mono.just(
      ClientResponse.create(org.springframework.http.HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(body)
        .build()
    );

    WebClient webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();

    ExerciseApiClient client = new ExerciseApiClient(webClient, new ObjectMapper(), "key", "fallback", 1, 0);
    Map<String, String> result = client.fetchExerciseImages(List.of("Jump"));

    assertEquals("http://gif", result.get("jump"));
  }
}

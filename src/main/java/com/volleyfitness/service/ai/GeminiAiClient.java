package com.volleyfitness.service.ai;

import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.exception.AiGenerationException;
import com.volleyfitness.exception.InvalidAiResponseException;
import com.volleyfitness.service.interfaces.AiService;
import com.volleyfitness.util.AiResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

@Service
public class GeminiAiClient implements AiService {
  private static final Logger log = LoggerFactory.getLogger(GeminiAiClient.class);

  private final ChatClient chatClient;
  private final AiResponseValidator validator = new AiResponseValidator();

  public GeminiAiClient(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  @Override
  public AiPlanResponse generatePlan(ProfileResponse profile) {
    String prompt = buildPrompt(profile);

    try {
      AiPlanResponse response = chatClient.prompt()
        .user(prompt)
        .call()
        .entity(AiPlanResponse.class);
      validator.validate(response);
      return response;
    } catch (InvalidAiResponseException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("AI generation failed", ex);
      throw new AiGenerationException("AI generation failed");
    }
  }

  private String buildPrompt(ProfileResponse profile) {
    return "You are an AI that returns STRICT JSON only. Generate a 1-day volleyball workout and diet plan. " +
      "No markdown. No explanations. No image URLs. " +
      "User profile: age=" + profile.age() + ", height=" + profile.height() + ", weight=" + profile.weight() +
      ", gender=" + profile.gender() + ", goal=" + profile.goal() + ", skillLevel=" + profile.skillLevel() +
      ", position=" + profile.position() + ". " +
      "Return JSON with keys workoutPlan and dietPlan in the schema provided.";
  }
}

package com.volleyfitness;

import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.exception.InvalidAiResponseException;
import com.volleyfitness.util.AiResponseValidator;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiResponseValidatorTest {
  @Test
  void validatesGoodResponse() {
    AiPlanResponse response = new AiPlanResponse(
      List.of(new AiPlanResponse.WorkoutSession("Session", "Focus",
        List.of(new AiPlanResponse.Exercise("Jump", "3", "10", "60s")))),
      new AiPlanResponse.DietPlan(2000, 120, 200, 60,
        List.of(new AiPlanResponse.Meal("Breakfast", List.of("Oats"))))
    );

    AiResponseValidator validator = new AiResponseValidator();
    assertDoesNotThrow(() -> validator.validate(response));
  }

  @Test
  void rejectsMissingMeals() {
    AiPlanResponse response = new AiPlanResponse(
      List.of(new AiPlanResponse.WorkoutSession("Session", "Focus",
        List.of(new AiPlanResponse.Exercise("Jump", "3", "10", "60s")))),
      new AiPlanResponse.DietPlan(2000, 120, 200, 60, List.of())
    );

    AiResponseValidator validator = new AiResponseValidator();
    assertThrows(InvalidAiResponseException.class, () -> validator.validate(response));
  }
}

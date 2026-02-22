package com.volleyfitness.util;

import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.exception.InvalidAiResponseException;
import java.util.List;

public class AiResponseValidator {
  public void validate(AiPlanResponse response) {
    if (response == null || response.workoutPlan() == null || response.dietPlan() == null) {
      throw new InvalidAiResponseException("AI response missing top-level fields");
    }

    if (response.workoutPlan().isEmpty()) {
      throw new InvalidAiResponseException("AI response missing workouts");
    }

    response.workoutPlan().forEach(session -> {
      if (isBlank(session.sessionName()) || isBlank(session.focusArea()) || session.exercises() == null) {
        throw new InvalidAiResponseException("AI response has invalid workout session");
      }
      if (session.exercises().isEmpty()) {
        throw new InvalidAiResponseException("AI response missing exercises");
      }
      session.exercises().forEach(ex -> {
        if (isBlank(ex.name()) || isBlank(ex.sets()) || isBlank(ex.reps()) || isBlank(ex.rest())) {
          throw new InvalidAiResponseException("AI response has invalid exercise fields");
        }
      });
    });

    AiPlanResponse.DietPlan diet = response.dietPlan();
    if (diet.meals() == null || diet.meals().isEmpty()) {
      throw new InvalidAiResponseException("AI response missing meals");
    }
    diet.meals().forEach(meal -> {
      if (isBlank(meal.mealName()) || meal.items() == null) {
        throw new InvalidAiResponseException("AI response has invalid meal");
      }
    });
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}

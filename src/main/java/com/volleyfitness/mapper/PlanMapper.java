package com.volleyfitness.mapper;

import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.dto.DailyPlanResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PlanMapper {
  public DailyPlanResponse toDailyPlan(LocalDate date, AiPlanResponse ai, Map<String, String> images) {
    List<DailyPlanResponse.WorkoutSession> sessions = ai.workoutPlan().stream()
      .map(session -> new DailyPlanResponse.WorkoutSession(
        session.sessionName(),
        session.focusArea(),
        session.exercises().stream()
          .map(ex -> new DailyPlanResponse.Exercise(
            ex.name(),
            ex.sets(),
            ex.reps(),
            ex.rest(),
            images.getOrDefault(ex.name().toLowerCase(), images.getOrDefault("fallback", null))
          ))
          .toList()
      ))
      .toList();

    AiPlanResponse.DietPlan diet = ai.dietPlan();
    DailyPlanResponse.DietPlan dietPlan = new DailyPlanResponse.DietPlan(
      diet.totalCalories(),
      diet.protein(),
      diet.carbs(),
      diet.fats(),
      diet.meals().stream()
        .map(meal -> new DailyPlanResponse.Meal(meal.mealName(), meal.items()))
        .toList()
    );

    return new DailyPlanResponse(date, sessions, dietPlan);
  }
}

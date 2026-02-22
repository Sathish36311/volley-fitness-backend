package com.volleyfitness.dto;

import java.util.List;

public record AiPlanResponse(
  List<WorkoutSession> workoutPlan,
  DietPlan dietPlan
) {
  public record WorkoutSession(
    String sessionName,
    String focusArea,
    List<Exercise> exercises
  ) {}

  public record Exercise(
    String name,
    String sets,
    String reps,
    String rest
  ) {}

  public record DietPlan(
    int totalCalories,
    int protein,
    int carbs,
    int fats,
    List<Meal> meals
  ) {}

  public record Meal(
    String mealName,
    List<String> items
  ) {}
}

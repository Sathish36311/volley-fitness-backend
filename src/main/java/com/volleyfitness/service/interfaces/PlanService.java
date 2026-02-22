package com.volleyfitness.service.interfaces;

import com.volleyfitness.dto.DailyPlanResponse;

public interface PlanService {
  DailyPlanResponse getTodayPlan(String email);
  DailyPlanResponse generateTodayPlan(String email);
}

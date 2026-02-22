package com.volleyfitness.controller;

import com.volleyfitness.dto.DailyPlanResponse;
import com.volleyfitness.service.interfaces.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
  private final PlanService planService;

  public PlanController(PlanService planService) {
    this.planService = planService;
  }

  @GetMapping("/today")
  public ResponseEntity<DailyPlanResponse> today(Authentication authentication) {
    return ResponseEntity.ok(planService.getTodayPlan(authentication.getName()));
  }

  @PostMapping("/generate")
  public ResponseEntity<DailyPlanResponse> generate(Authentication authentication) {
    return ResponseEntity.ok(planService.generateTodayPlan(authentication.getName()));
  }
}

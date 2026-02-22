package com.volleyfitness.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volleyfitness.domain.Plan;
import com.volleyfitness.domain.Profile;
import com.volleyfitness.domain.User;
import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.dto.DailyPlanResponse;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.exception.ProfileNotCompletedException;
import com.volleyfitness.exception.RateLimitException;
import com.volleyfitness.mapper.PlanMapper;
import com.volleyfitness.repository.PlanRepository;
import com.volleyfitness.repository.ProfileRepository;
import com.volleyfitness.repository.UserRepository;
import com.volleyfitness.service.interfaces.AiService;
import com.volleyfitness.service.interfaces.ExerciseService;
import com.volleyfitness.service.interfaces.PlanService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanServiceImpl implements PlanService {
  private final PlanRepository planRepository;
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final AiService aiService;
  private final ExerciseService exerciseService;
  private final ObjectMapper objectMapper;
  private final PlanMapper mapper = new PlanMapper();

  public PlanServiceImpl(PlanRepository planRepository,
                         UserRepository userRepository,
                         ProfileRepository profileRepository,
                         AiService aiService,
                         ExerciseService exerciseService,
                         ObjectMapper objectMapper) {
    this.planRepository = planRepository;
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
    this.aiService = aiService;
    this.exerciseService = exerciseService;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public DailyPlanResponse getTodayPlan(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    LocalDate today = LocalDate.now();

    return planRepository.findByUserAndPlanDate(user, today)
      .map(plan -> readPlan(plan.getPlanJson()))
      .orElse(null);
  }

  @Override
  @Transactional
  public DailyPlanResponse generateTodayPlan(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    LocalDate today = LocalDate.now();

//    if (planRepository.findByUserAndPlanDate(user, today).isPresent()) {
//      throw new RateLimitException("Plan already generated for today");
//    }

    Profile profile = profileRepository.findByUser(user)
      .orElseThrow(() -> new ProfileNotCompletedException("Profile must be completed"));

    ProfileResponse profileResponse = new ProfileResponse(
      profile.getAge(),
      profile.getHeight(),
      profile.getWeight(),
      profile.getGender(),
      profile.getGoal(),
      profile.getSkillLevel(),
      profile.getPosition()
    );

    AiPlanResponse aiResponse = aiService.generatePlan(profileResponse);

    List<String> exerciseNames = aiResponse.workoutPlan().stream()
      .flatMap(session -> session.exercises().stream())
      .map(AiPlanResponse.Exercise::name)
      .collect(Collectors.toList());

    Map<String, String> images = exerciseService.fetchExerciseImages(exerciseNames);
    DailyPlanResponse dailyPlan = mapper.toDailyPlan(today, aiResponse, images);

    try {
      String planJson = objectMapper.writeValueAsString(dailyPlan);
      planRepository.save(new Plan(user, today, planJson));
      return dailyPlan;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to save plan");
    }
  }

  private DailyPlanResponse readPlan(String json) {
    try {
      return objectMapper.readValue(json, DailyPlanResponse.class);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to read plan");
    }
  }
}

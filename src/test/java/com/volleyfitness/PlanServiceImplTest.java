package com.volleyfitness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volleyfitness.domain.Plan;
import com.volleyfitness.domain.Profile;
import com.volleyfitness.domain.User;
import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.dto.DailyPlanResponse;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.exception.ProfileNotCompletedException;
import com.volleyfitness.exception.RateLimitException;
import com.volleyfitness.repository.PlanRepository;
import com.volleyfitness.repository.ProfileRepository;
import com.volleyfitness.repository.UserRepository;
import com.volleyfitness.service.impl.PlanServiceImpl;
import com.volleyfitness.service.interfaces.AiService;
import com.volleyfitness.service.interfaces.ExerciseService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanServiceImplTest {
  private PlanRepository planRepository;
  private UserRepository userRepository;
  private ProfileRepository profileRepository;
  private AiService aiService;
  private ExerciseService exerciseService;
  private PlanServiceImpl planService;

  @BeforeEach
  void setup() {
    planRepository = mock(PlanRepository.class);
    userRepository = mock(UserRepository.class);
    profileRepository = mock(ProfileRepository.class);
    aiService = mock(AiService.class);
    exerciseService = mock(ExerciseService.class);
    planService = new PlanServiceImpl(planRepository, userRepository, profileRepository, aiService, exerciseService, new ObjectMapper());
  }

  @Test
  void blocksDuplicatePlanPerDay() {
    User user = new User("a@b.com", "pass");
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(planRepository.findByUserAndPlanDate(eq(user), any(LocalDate.class))).thenReturn(Optional.of(new Plan(user, LocalDate.now(), "{}")));

    assertThrows(RateLimitException.class, () -> planService.generateTodayPlan("a@b.com"));
  }

  @Test
  void requiresProfileCompletion() {
    User user = new User("a@b.com", "pass");
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(planRepository.findByUserAndPlanDate(eq(user), any(LocalDate.class))).thenReturn(Optional.empty());
    when(profileRepository.findByUser(user)).thenReturn(Optional.empty());

    assertThrows(ProfileNotCompletedException.class, () -> planService.generateTodayPlan("a@b.com"));
  }

  @Test
  void storesGeneratedPlan() {
    User user = new User("a@b.com", "pass");
    Profile profile = new Profile(user);
    profile.setAge(20);
    profile.setHeight(180);
    profile.setWeight(75);
    profile.setGender("M");
    profile.setGoal("Power");
    profile.setSkillLevel("Intermediate");
    profile.setPosition("Setter");

    AiPlanResponse aiResponse = new AiPlanResponse(
      List.of(new AiPlanResponse.WorkoutSession("Session", "Focus",
        List.of(new AiPlanResponse.Exercise("Jump Squat", "3", "10", "60s")))),
      new AiPlanResponse.DietPlan(2000, 120, 200, 60,
        List.of(new AiPlanResponse.Meal("Breakfast", List.of("Oats"))))
    );

    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(planRepository.findByUserAndPlanDate(eq(user), any(LocalDate.class))).thenReturn(Optional.empty());
    when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
    when(aiService.generatePlan(any(ProfileResponse.class))).thenReturn(aiResponse);
    when(exerciseService.fetchExerciseImages(any())).thenReturn(Map.of("jump squat", "gif", "fallback", "fallback"));

    DailyPlanResponse plan = planService.generateTodayPlan("a@b.com");
    assertNotNull(plan);
    verify(planRepository, times(1)).save(any(Plan.class));
  }
}

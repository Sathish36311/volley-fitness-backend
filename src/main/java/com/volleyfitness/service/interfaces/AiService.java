package com.volleyfitness.service.interfaces;

import com.volleyfitness.dto.AiPlanResponse;
import com.volleyfitness.dto.ProfileResponse;

public interface AiService {
  AiPlanResponse generatePlan(ProfileResponse profile);
}

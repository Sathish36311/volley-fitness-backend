package com.volleyfitness.service.interfaces;

import com.volleyfitness.dto.ProfileRequest;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.dto.ProfileStatusResponse;

public interface ProfileService {
  ProfileResponse getProfile(String email);
  ProfileResponse upsertProfile(String email, ProfileRequest request);
  ProfileStatusResponse getStatus(String email);
}

package com.volleyfitness.service.impl;

import com.volleyfitness.domain.Profile;
import com.volleyfitness.domain.User;
import com.volleyfitness.dto.ProfileRequest;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.dto.ProfileStatusResponse;
import com.volleyfitness.repository.ProfileRepository;
import com.volleyfitness.repository.UserRepository;
import com.volleyfitness.service.interfaces.ProfileService;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {
  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;

  public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
    this.profileRepository = profileRepository;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileResponse getProfile(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Optional<Profile> profile = profileRepository.findByUser(user);
    return profile.map(this::toResponse).orElse(null);
  }

  @Override
  @Transactional
  public ProfileResponse upsertProfile(String email, ProfileRequest request) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Profile profile = profileRepository.findByUser(user).orElseGet(() -> new Profile(user));
    profile.setAge(request.age());
    profile.setHeight(request.height());
    profile.setWeight(request.weight());
    profile.setGender(request.gender());
    profile.setGoal(request.goal());
    profile.setSkillLevel(request.skillLevel());
    profile.setPosition(request.position());
    profileRepository.save(profile);

    return toResponse(profile);
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileStatusResponse getStatus(String email) {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    boolean completed = profileRepository.findByUser(user).isPresent();
    return new ProfileStatusResponse(completed);
  }

  private ProfileResponse toResponse(Profile profile) {
    return new ProfileResponse(
      profile.getAge(),
      profile.getHeight(),
      profile.getWeight(),
      profile.getGender(),
      profile.getGoal(),
      profile.getSkillLevel(),
      profile.getPosition()
    );
  }
}

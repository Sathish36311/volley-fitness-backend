package com.volleyfitness.controller;

import com.volleyfitness.dto.ProfileRequest;
import com.volleyfitness.dto.ProfileResponse;
import com.volleyfitness.dto.ProfileStatusResponse;
import com.volleyfitness.service.interfaces.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping
  public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
    return ResponseEntity.ok(profileService.getProfile(authentication.getName()));
  }

  @GetMapping("/status")
  public ResponseEntity<ProfileStatusResponse> status(Authentication authentication) {
    return ResponseEntity.ok(profileService.getStatus(authentication.getName()));
  }

  @PostMapping
  public ResponseEntity<ProfileResponse> upsert(@Valid @RequestBody ProfileRequest request,
                                                Authentication authentication) {
    return ResponseEntity.ok(profileService.upsertProfile(authentication.getName(), request));
  }
}

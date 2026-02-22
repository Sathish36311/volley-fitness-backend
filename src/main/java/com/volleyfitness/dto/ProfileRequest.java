package com.volleyfitness.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProfileRequest(
  @Min(10) int age,
  @Min(100) int height,
  @Min(30) int weight,
  @NotBlank String gender,
  @NotBlank String goal,
  @NotBlank String skillLevel,
  @NotBlank String position
) {}

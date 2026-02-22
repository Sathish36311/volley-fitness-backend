package com.volleyfitness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
  @Email @NotBlank String email,
  @NotBlank
  @Size(min = 8, max = 72)
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
    message = "Password must contain uppercase, lowercase, and a number"
  )
  String password
) {}

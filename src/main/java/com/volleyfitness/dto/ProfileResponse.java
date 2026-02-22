package com.volleyfitness.dto;

public record ProfileResponse(
  int age,
  int height,
  int weight,
  String gender,
  String goal,
  String skillLevel,
  String position
) {}

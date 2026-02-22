package com.volleyfitness.service.interfaces;

import java.util.Map;

public interface ExerciseService {
  Map<String, String> fetchExerciseImages(Iterable<String> names);
}

package com.volleyfitness.exception;

public class ProfileNotCompletedException extends RuntimeException {
  public ProfileNotCompletedException(String message) {
    super(message);
  }
}

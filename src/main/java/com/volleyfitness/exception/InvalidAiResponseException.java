package com.volleyfitness.exception;

public class InvalidAiResponseException extends RuntimeException {
  public InvalidAiResponseException(String message) {
    super(message);
  }
}

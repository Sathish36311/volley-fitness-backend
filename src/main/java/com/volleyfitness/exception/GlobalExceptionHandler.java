package com.volleyfitness.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ProfileNotCompletedException.class)
  public ResponseEntity<ApiError> handleProfile(ProfileNotCompletedException ex) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<ApiError> handleRateLimit(RateLimitException ex) {
    return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
  }

  @ExceptionHandler({AiGenerationException.class, ExerciseApiException.class, InvalidAiResponseException.class})
  public ResponseEntity<ApiError> handleAi(RuntimeException ex) {
    return build(HttpStatus.BAD_GATEWAY, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
      .map(err -> err.getField() + ": " + err.getDefaultMessage())
      .collect(Collectors.joining(", "));
    return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation failed" : message);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
    return build(HttpStatus.UNAUTHORIZED, "Invalid credentials");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleOther(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(new ApiError(status.value(), message, OffsetDateTime.now()));
  }

  public record ApiError(int status, String message, OffsetDateTime timestamp) {}
}

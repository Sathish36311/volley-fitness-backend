package com.volleyfitness.service.interfaces;

import com.volleyfitness.dto.AuthResponse;
import com.volleyfitness.dto.LoginRequest;
import com.volleyfitness.dto.RegisterRequest;

public interface AuthService {
  void register(RegisterRequest request);
  AuthResponse login(LoginRequest request);
}

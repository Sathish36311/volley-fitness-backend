package com.volleyfitness.service.impl;

import com.volleyfitness.dto.AuthResponse;
import com.volleyfitness.dto.LoginRequest;
import com.volleyfitness.dto.RegisterRequest;
import com.volleyfitness.repository.UserRepository;
import com.volleyfitness.security.JwtService;
import com.volleyfitness.service.interfaces.AuthService;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthServiceImpl(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Override
  @Transactional
  public void register(RegisterRequest request) {
    String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new IllegalArgumentException("Email already registered");
    }

    String hashed = passwordEncoder.encode(request.password());
    userRepository.save(new com.volleyfitness.domain.User(normalizedEmail, hashed));
  }

  @Override
  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.email().trim().toLowerCase(Locale.ROOT),
        request.password()
      )
    );

    String token = jwtService.generateToken(authentication.getName());
    return new AuthResponse(token);
  }
}

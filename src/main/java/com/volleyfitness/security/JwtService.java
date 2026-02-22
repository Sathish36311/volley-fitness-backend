package com.volleyfitness.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
    if (secret == null || secret.length() < 32) {
      throw new IllegalStateException("app.jwt.secret must be at least 32 characters");
    }
    if (expirationMinutes <= 0) {
      throw new IllegalStateException("app.jwt.expiration-minutes must be greater than 0");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(String email) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(email)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, String email) {
    try {
      String subject = extractUsername(token);
      return subject.equals(email) && !parseClaims(token).getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException ex) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }
}

package com.volleyfitness.repository;

import com.volleyfitness.domain.Profile;
import com.volleyfitness.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
  Optional<Profile> findByUser(User user);
}

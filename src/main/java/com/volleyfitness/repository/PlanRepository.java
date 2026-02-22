package com.volleyfitness.repository;

import com.volleyfitness.domain.Plan;
import com.volleyfitness.domain.User;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
  Optional<Plan> findByUserAndPlanDate(User user, LocalDate planDate);
}

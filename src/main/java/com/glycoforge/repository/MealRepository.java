package com.glycoforge.repository;

import com.glycoforge.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByUserIdOrderByEatenAtDesc(Long userId);
}


package com.glycoforge.repository;

import com.glycoforge.domain.Injection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface InjectionRepository extends JpaRepository<Injection, Long> {
    List<Injection> findByUserIdOrderByInjectedAtDesc(Long userId);
    Optional<Injection> findTopByUserIdOrderByInjectedAtDesc(Long userId);
}


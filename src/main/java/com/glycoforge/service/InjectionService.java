package com.glycoforge.service;

import com.glycoforge.domain.Injection;
import com.glycoforge.domain.User;
import com.glycoforge.dto.InjectionDto;
import com.glycoforge.dto.ManualInjectionRequest;
import com.glycoforge.repository.InjectionRepository;
import com.glycoforge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InjectionService {

    private final InjectionRepository injectionRepository;
    private final UserRepository userRepository;

    public InjectionService(InjectionRepository injectionRepository, UserRepository userRepository) {
        this.injectionRepository = injectionRepository;
        this.userRepository = userRepository;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentPrincipalName));
    }

    @Transactional
    public InjectionDto createManualInjection(ManualInjectionRequest request) {
        User user = getCurrentUser();
        Injection injection = new Injection();
        injection.setUser(user);
        injection.setUnits(request.getUnits());
        // Use provided time or default to now
        injection.setInjectedAt(request.getInjectedAt() != null ? request.getInjectedAt() : LocalDateTime.now());

        Injection savedInjection = injectionRepository.save(injection);
        return mapToInjectionDto(savedInjection);
    }

    @Transactional(readOnly = true)
    public List<InjectionDto> getInjectionsForCurrentUser() {
        User user = getCurrentUser();
        return injectionRepository.findByUserIdOrderByInjectedAtDesc(user.getId())
                .stream()
                .map(this::mapToInjectionDto)
                .collect(Collectors.toList());
    }

    // Mapper method
    private InjectionDto mapToInjectionDto(Injection injection) {
        return new InjectionDto(
                injection.getId(),
                injection.getUnits(),
                injection.getInjectedAt()
        );
    }
}


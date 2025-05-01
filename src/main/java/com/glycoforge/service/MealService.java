package com.glycoforge.service;

import com.glycoforge.domain.Meal;
import com.glycoforge.domain.User;
import com.glycoforge.dto.MealDto;
import com.glycoforge.dto.MealRequest;
import com.glycoforge.repository.MealRepository;
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
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    // Placeholder for LLM client/service
    // private final LlmClient llmClient;

    public MealService(MealRepository mealRepository, UserRepository userRepository /*, LlmClient llmClient */) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        // this.llmClient = llmClient;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentPrincipalName));
    }

    @Transactional
    public MealDto createMeal(MealRequest mealRequest) {
        User user = getCurrentUser();
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setDescription(mealRequest.getFreeTextDescription());
        meal.setEatenAt(LocalDateTime.now()); // Set current time as eaten time

        // *** Placeholder for LLM integration ***
        // Here you would call the LLM service to parse mealRequest.getFreeTextDescription()
        // and populate gramsCarbs, gramsProtein, gramsFat.
        // Example placeholder values:
        meal.setGramsCarbs(50.0); // Placeholder
        meal.setGramsProtein(20.0); // Placeholder
        meal.setGramsFat(15.0); // Placeholder
        // meal = llmClient.parseMealDescription(meal);

        Meal savedMeal = mealRepository.save(meal);
        return mapToMealDto(savedMeal);
    }

    @Transactional(readOnly = true)
    public List<MealDto> getMealsForCurrentUser() {
        User user = getCurrentUser();
        return mealRepository.findByUserIdOrderByEatenAtDesc(user.getId())
                .stream()
                .map(this::mapToMealDto)
                .collect(Collectors.toList());
    }

    // Mapper method
    private MealDto mapToMealDto(Meal meal) {
        return new MealDto(
                meal.getId(),
                meal.getDescription(),
                meal.getGramsCarbs(),
                meal.getGramsProtein(),
                meal.getGramsFat(),
                meal.getEatenAt()
        );
    }
}


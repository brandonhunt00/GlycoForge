package com.glycoforge.service;

import com.glycoforge.domain.Meal;
import com.glycoforge.domain.User;
import com.glycoforge.dto.RecommendationDto;
import com.glycoforge.repository.MealRepository;
import com.glycoforge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    // Define a target glucose level (e.g., 100 mg/dL). This could be configurable per user later.
    private static final double TARGET_GLUCOSE = 100.0;

    public RecommendationService(UserRepository userRepository, MealRepository mealRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentPrincipalName));
    }

    @Transactional(readOnly = true)
    public RecommendationDto getLatestRecommendation() {
        User user = getCurrentUser();
        // Find the most recent meal for the user
        Meal latestMeal = mealRepository.findByUserIdOrderByEatenAtDesc(user.getId())
                .stream()
                .findFirst()
                .orElse(null); // Handle case where user has no meals yet

        if (latestMeal == null || latestMeal.getGramsCarbs() == null || user.getInsulinRatio() == null || user.getSensitivityFactor() == null) {
            // Cannot calculate recommendation if essential data is missing
            // Return a DTO indicating this, or throw an exception
            return new RecommendationDto(null, null, "Missing user data (ratio/sensitivity) or meal data (carbs) for calculation.", null, null);
        }

        // Core Algorithm
        double totalCarbs = latestMeal.getGramsCarbs();
        double insulinRatio = user.getInsulinRatio(); // Units per 10g carbs
        double sensitivityFactor = user.getSensitivityFactor(); // mg/dL per unit

        // 1. Carb Dose Calculation
        // Ensure insulinRatio is not zero to avoid division by zero
        double carbDose = (insulinRatio > 0) ? (totalCarbs / 10.0) * insulinRatio : 0.0;

        // 2. Correction Dose Calculation (Placeholder)
        // Requires currentGlucose, which is not available in this model.
        // Assuming correctionDose is 0 for now.
        // double currentGlucose = ?; // Needs input or integration with CGM/BGM
        double correctionDose = 0.0;
        // if (sensitivityFactor > 0) {
        //     correctionDose = (currentGlucose - TARGET_GLUCOSE) / sensitivityFactor;
        // }
        // Ensure correction dose is not negative (don't correct for being below target)
        // correctionDose = Math.max(0, correctionDose);

        // 3. Total Dose
        double totalDoseRaw = carbDose + correctionDose;
        double totalDoseRounded = Math.round(totalDoseRaw * 2) / 2.0; // Round to nearest 0.5 unit

        // 4. Next Injection Time
        LocalDateTime nextInjectionTime = latestMeal.getEatenAt().plusHours(4);

        String details = String.format(
            "Based on meal at %s (%.1fg carbs). Carb dose: %.1f units (%.1fg carbs / 10g * %.2f ratio). Correction dose: %.1f units (Placeholder). Total: %.1f units.",
            latestMeal.getEatenAt(), totalCarbs, carbDose, totalCarbs, insulinRatio, correctionDose, totalDoseRounded
        );

        return new RecommendationDto(
                totalDoseRounded,
                nextInjectionTime,
                details,
                latestMeal.getId(),
                latestMeal.getEatenAt()
        );
    }

    // Note: This service currently calculates based *only* on the last meal.
    // A more sophisticated approach would consider Insulin On Board (IOB) from previous injections.
}


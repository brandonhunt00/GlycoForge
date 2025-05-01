package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private Double totalDose;
    private LocalDateTime nextInjectionTime; // Based on last meal time + 4 hours
    private String calculationDetails; // Optional: Explain how the dose was calculated
    private Long basedOnMealId; // ID of the meal used for calculation
    private LocalDateTime mealEatenAt;
}


package com.glycoforge.controller;

import com.glycoforge.dto.RecommendationDto;
import com.glycoforge.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/latest")
    public ResponseEntity<RecommendationDto> getLatestRecommendation() {
        try {
            RecommendationDto recommendation = recommendationService.getLatestRecommendation();
            // Check if calculation was possible
            if (recommendation.getTotalDose() == null && recommendation.getNextInjectionTime() == null) {
                // Return a specific status or message if calculation failed due to missing data
                // For now, returning the DTO with the explanation message and 200 OK
                return ResponseEntity.ok(recommendation);
            }
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            // Handle appropriately (e.g., user not found)
            return ResponseEntity.status(401).build(); // Unauthorized if user context issue
        }
    }
}


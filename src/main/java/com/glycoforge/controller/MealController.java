package com.glycoforge.controller;

import com.glycoforge.dto.MealDto;
import com.glycoforge.dto.MealRequest;
import com.glycoforge.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealDto> createMeal(@RequestBody MealRequest mealRequest) {
        try {
            MealDto createdMeal = mealService.createMeal(mealRequest);
            // Consider returning 201 Created with location header
            return ResponseEntity.ok(createdMeal);
        } catch (Exception e) {
            // Handle appropriately
            return ResponseEntity.badRequest().build(); // Or more specific error
        }
    }

    // Endpoint to get meals for the sidebar (assuming this is needed)
    @GetMapping("/history") // Or just GET /api/meal ? Depends on UI needs
    public ResponseEntity<List<MealDto>> getMealHistory() {
         try {
            List<MealDto> meals = mealService.getMealsForCurrentUser();
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            // Handle appropriately
            return ResponseEntity.status(401).build(); // Unauthorized if user not found
        }
    }
}


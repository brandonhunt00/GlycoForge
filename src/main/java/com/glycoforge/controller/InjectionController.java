package com.glycoforge.controller;

import com.glycoforge.dto.InjectionDto;
import com.glycoforge.dto.ManualInjectionRequest;
import com.glycoforge.service.InjectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/injection")
public class InjectionController {

    private final InjectionService injectionService;

    public InjectionController(InjectionService injectionService) {
        this.injectionService = injectionService;
    }

    @PostMapping("/manual")
    public ResponseEntity<InjectionDto> createManualInjection(@RequestBody ManualInjectionRequest request) {
        try {
            InjectionDto createdInjection = injectionService.createManualInjection(request);
            // Consider returning 201 Created
            return ResponseEntity.ok(createdInjection);
        } catch (Exception e) {
            // Handle appropriately
            return ResponseEntity.badRequest().build(); // Or more specific error
        }
    }

    // Endpoint to get injection history for the sidebar
    @GetMapping("/history") // Or just GET /api/injection ?
    public ResponseEntity<List<InjectionDto>> getInjectionHistory() {
        try {
            List<InjectionDto> injections = injectionService.getInjectionsForCurrentUser();
            return ResponseEntity.ok(injections);
        } catch (Exception e) {
            // Handle appropriately
            return ResponseEntity.status(401).build(); // Unauthorized if user not found
        }
    }
}


package com.glycoforge.controller;

import com.glycoforge.dto.AuthResponse;
import com.glycoforge.dto.LoginRequest;
import com.glycoforge.dto.RegisterRequest;
import com.glycoforge.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Consider returning the created User DTO or just a success message
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            // Basic error handling, replace with global exception handler later
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Basic error handling, replace with global exception handler later
            // Return 401 Unauthorized for bad credentials
            return ResponseEntity.status(401).build();
        }
    }
}


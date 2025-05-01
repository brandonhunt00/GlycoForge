package com.glycoforge.controller;

import com.glycoforge.dto.UserDto;
import com.glycoforge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            UserDto userDto = userService.getCurrentUserDetails();
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            // Handle appropriately, e.g., user not found or not authenticated
            // This should ideally be handled by the global exception handler
            return ResponseEntity.status(401).build(); // Or 404 if user context exists but DB lookup fails
        }
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateCurrentUserDetails(userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            // Handle appropriately
            return ResponseEntity.badRequest().build(); // Or more specific error
        }
    }
}


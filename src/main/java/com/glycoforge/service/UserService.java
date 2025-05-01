package com.glycoforge.service;

import com.glycoforge.domain.User;
import com.glycoforge.dto.UserDto;
import com.glycoforge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Helper method to get the current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentPrincipalName));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserDetails() {
        User user = getCurrentUser();
        return mapToUserDto(user);
    }

    @Transactional
    public UserDto updateCurrentUserDetails(UserDto userDto) {
        User user = getCurrentUser();

        // Update mutable fields - ensure only allowed fields are updated
        // Password should be updated via a separate mechanism if needed
        if (userDto.getAge() != null) user.setAge(userDto.getAge());
        if (userDto.getHeightCm() != null) user.setHeightCm(userDto.getHeightCm());
        if (userDto.getWeightKg() != null) user.setWeightKg(userDto.getWeightKg());
        if (userDto.getInsulinRatio() != null) user.setInsulinRatio(userDto.getInsulinRatio());
        if (userDto.getSensitivityFactor() != null) user.setSensitivityFactor(userDto.getSensitivityFactor());
        // Email is the username, typically not updated this way.

        User updatedUser = userRepository.save(user);
        return mapToUserDto(updatedUser);
    }

    // Mapper method (consider using MapStruct for more complex scenarios)
    private UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getAge(),
                user.getHeightCm(),
                user.getWeightKg(),
                user.getInsulinRatio(),
                user.getSensitivityFactor()
        );
    }
}


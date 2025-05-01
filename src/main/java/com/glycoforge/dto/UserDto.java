package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private Integer age;
    private Integer heightCm;
    private Double weightKg;
    private Double insulinRatio;
    private Double sensitivityFactor;
}


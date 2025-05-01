package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String pwd; // Raw password, will be hashed in service
    private Integer age;
    private Integer heightCm;
    private Double weightKg;
    private Double insulinRatio;
    private Double sensitivityFactor;
}


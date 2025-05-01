package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {
    private Long id;
    private String description;
    private Double gramsCarbs;
    private Double gramsProtein;
    private Double gramsFat;
    private LocalDateTime eatenAt;
}


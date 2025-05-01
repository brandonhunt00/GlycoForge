package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjectionDto {
    private Long id;
    private Double units;
    private LocalDateTime injectedAt;
}


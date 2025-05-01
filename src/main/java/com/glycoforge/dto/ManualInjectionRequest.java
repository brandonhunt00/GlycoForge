package com.glycoforge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualInjectionRequest {
    private Double units;
    private LocalDateTime injectedAt; // Allow user to specify time, default to now if null?
}


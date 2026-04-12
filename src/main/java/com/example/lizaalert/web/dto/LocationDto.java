package com.example.lizaalert.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record LocationDto(
    @Size(max = 120) String city,
    @Size(max = 120) String district,
    @Size(max = 255) String address,
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") BigDecimal latitude,
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") BigDecimal longitude,
    @Size(max = 1000) String notes
) {
}


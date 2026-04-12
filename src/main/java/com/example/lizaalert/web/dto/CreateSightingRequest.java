package com.example.lizaalert.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateSightingRequest(
    @NotBlank @Size(max = 150) String reporterName,
    @Size(max = 40) String reporterPhone,
    @NotNull Instant seenAt,
    @Min(0) @Max(100) Integer confidenceScore,
    @Valid LocationDto location,
    @NotBlank @Size(max = 2000) String notes
) {
}


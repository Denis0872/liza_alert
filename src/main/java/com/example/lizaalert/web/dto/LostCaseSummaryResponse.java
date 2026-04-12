package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.CaseStatus;
import java.time.Instant;
import java.util.UUID;

public record LostCaseSummaryResponse(
    UUID id,
    CaseStatus status,
    Instant lostAt,
    boolean urgent,
    PetResponse pet,
    LocationDto lastSeenLocation
) {
}


package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.CaseStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LostCaseDetailsResponse(
    UUID id,
    CaseStatus status,
    Instant lostAt,
    String circumstances,
    String contactPhone,
    String rewardDetails,
    boolean urgent,
    PetResponse pet,
    UserContactResponse owner,
    LocationDto lastSeenLocation,
    List<SightingResponse> sightings,
    List<MediaAssetResponse> media
) {
}


package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.MediaType;
import com.example.lizaalert.domain.model.PetSex;
import com.example.lizaalert.domain.model.PetSpecies;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public record CreateLostCaseRequest(
    @Valid @NotNull OwnerRequest owner,
    @Valid @NotNull PetRequest pet,
    @Valid @NotNull CaseRequest caseData,
    @Valid List<MediaRequest> media
) {

    public record OwnerRequest(
        @NotBlank @Size(max = 150) String fullName,
        @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 40) String phone,
        @Size(max = 120) String telegramHandle
    ) {
    }

    public record PetRequest(
        @NotNull PetSpecies species,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 120) String breed,
        PetSex sex,
        @PositiveOrZero Integer ageYears,
        @Size(max = 40) String sizeLabel,
        @Size(max = 80) String primaryColor,
        @Size(max = 80) String secondaryColor,
        @Size(max = 2000) String specialMarks,
        @Size(max = 120) String microchipId,
        @Size(max = 255) String collarDetails,
        @Size(max = 2000) String behaviorNotes
    ) {
    }

    public record CaseRequest(
        @NotNull Instant lostAt,
        @Valid LocationDto lastSeenLocation,
        @NotBlank @Size(max = 4000) String circumstances,
        @NotBlank @Size(max = 40) String contactPhone,
        @Size(max = 500) String rewardDetails,
        boolean urgent
    ) {
    }

    public record MediaRequest(
        MediaType mediaType,
        @NotBlank @Size(max = 500) String externalUrl,
        @Size(max = 255) String caption
    ) {
    }
}


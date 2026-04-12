package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.PetSex;
import com.example.lizaalert.domain.model.PetSpecies;
import java.util.UUID;

public record PetResponse(
    UUID id,
    PetSpecies species,
    String name,
    String breed,
    PetSex sex,
    Integer ageYears,
    String sizeLabel,
    String primaryColor,
    String secondaryColor,
    String specialMarks,
    String microchipId,
    String collarDetails,
    String behaviorNotes
) {
}


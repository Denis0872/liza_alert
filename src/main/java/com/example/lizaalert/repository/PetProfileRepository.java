package com.example.lizaalert.repository;

import com.example.lizaalert.domain.model.PetProfile;
import com.example.lizaalert.domain.model.PetSpecies;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetProfileRepository extends JpaRepository<PetProfile, UUID> {

    List<PetProfile> findAllBySpecies(PetSpecies species);
}


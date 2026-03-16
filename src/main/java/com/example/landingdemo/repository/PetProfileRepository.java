package com.example.landingdemo.repository;

import com.example.landingdemo.domain.model.PetProfile;
import com.example.landingdemo.domain.model.PetSpecies;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetProfileRepository extends JpaRepository<PetProfile, UUID> {

    List<PetProfile> findAllBySpecies(PetSpecies species);
}

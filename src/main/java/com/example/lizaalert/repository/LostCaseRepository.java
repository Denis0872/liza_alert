package com.example.lizaalert.repository;

import com.example.lizaalert.domain.model.CaseStatus;
import com.example.lizaalert.domain.model.LostCase;
import com.example.lizaalert.domain.model.PetSpecies;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LostCaseRepository extends JpaRepository<LostCase, UUID> {

    @EntityGraph(attributePaths = {"petProfile", "petProfile.owner", "createdBy"})
    List<LostCase> findAllByStatus(CaseStatus status);

    @EntityGraph(attributePaths = {"petProfile", "petProfile.owner", "createdBy"})
    @Query("select lc from LostCase lc where lc.id = :id")
    Optional<LostCase> findDetailedById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"petProfile", "petProfile.owner", "createdBy"})
    List<LostCase> findAllByStatusAndPetProfileSpecies(CaseStatus status, PetSpecies species);
}


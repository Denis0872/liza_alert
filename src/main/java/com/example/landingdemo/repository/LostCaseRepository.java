package com.example.landingdemo.repository;

import com.example.landingdemo.domain.model.CaseStatus;
import com.example.landingdemo.domain.model.LostCase;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostCaseRepository extends JpaRepository<LostCase, UUID> {

    List<LostCase> findAllByStatus(CaseStatus status);
}

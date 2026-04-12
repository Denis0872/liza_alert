package com.example.lizaalert.repository;

import com.example.lizaalert.domain.model.MediaAsset;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

    List<MediaAsset> findAllByLostCaseIdOrderBySortOrderAsc(UUID lostCaseId);
}


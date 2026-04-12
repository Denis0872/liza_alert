package com.example.lizaalert.web;

import com.example.lizaalert.domain.model.CaseStatus;
import com.example.lizaalert.domain.model.PetSpecies;
import com.example.lizaalert.service.LostCaseService;
import com.example.lizaalert.web.dto.CreateLostCaseRequest;
import com.example.lizaalert.web.dto.CreateSightingRequest;
import com.example.lizaalert.web.dto.LostCaseDetailsResponse;
import com.example.lizaalert.web.dto.LostCaseSummaryResponse;
import com.example.lizaalert.web.dto.UpdateCaseStatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lost-cases")
public class LostCaseController {

    private final LostCaseService lostCaseService;

    public LostCaseController(LostCaseService lostCaseService) {
        this.lostCaseService = lostCaseService;
    }

    @GetMapping
    public List<LostCaseSummaryResponse> getCases(
        @RequestParam(required = false) CaseStatus status,
        @RequestParam(required = false) PetSpecies species
    ) {
        return lostCaseService.findCases(status, species);
    }

    @GetMapping("/{caseId}")
    public LostCaseDetailsResponse getCase(@PathVariable UUID caseId) {
        return lostCaseService.getCase(caseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LostCaseDetailsResponse createCase(@Valid @RequestBody CreateLostCaseRequest request) {
        return lostCaseService.createCase(request);
    }

    @PostMapping("/{caseId}/sightings")
    @ResponseStatus(HttpStatus.CREATED)
    public LostCaseDetailsResponse addSighting(@PathVariable UUID caseId, @Valid @RequestBody CreateSightingRequest request) {
        return lostCaseService.addSighting(caseId, request);
    }

    @PatchMapping("/{caseId}/status")
    public LostCaseDetailsResponse updateStatus(
        @PathVariable UUID caseId,
        @Valid @RequestBody UpdateCaseStatusRequest request
    ) {
        return lostCaseService.updateStatus(caseId, request);
    }
}


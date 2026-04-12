package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.CaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCaseStatusRequest(@NotNull CaseStatus status) {
}


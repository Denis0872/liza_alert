package com.example.lizaalert.web.dto;

import com.example.lizaalert.domain.model.MediaType;
import java.util.UUID;

public record MediaAssetResponse(
    UUID id,
    MediaType mediaType,
    String externalUrl,
    String caption,
    Integer sortOrder
) {
}


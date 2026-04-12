package com.example.lizaalert.web.dto;

import java.util.UUID;

public record UserContactResponse(
    UUID id,
    String fullName,
    String phone,
    String telegramHandle
) {
}


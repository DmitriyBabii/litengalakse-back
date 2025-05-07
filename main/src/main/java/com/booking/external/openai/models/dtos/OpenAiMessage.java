package com.booking.external.openai.models.dtos;

public record OpenAiMessage(
        String role,
        String content
) {
}

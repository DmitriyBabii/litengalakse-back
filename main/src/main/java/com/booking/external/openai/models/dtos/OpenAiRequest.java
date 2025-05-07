package com.booking.external.openai.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAiRequest(
        String model,
        List<OpenAiMessage> messages,
        @JsonProperty("max_tokens") Integer maxTokens,
        Double temperature
) {
}

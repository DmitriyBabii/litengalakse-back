package com.booking.external.openai.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OpenAiRequest {
    private String model;
    private List<OpenAiMessage> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Double temperature;
}

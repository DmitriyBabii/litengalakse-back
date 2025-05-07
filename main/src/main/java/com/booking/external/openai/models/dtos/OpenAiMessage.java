package com.booking.external.openai.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OpenAiMessage {
    private String role;
    private String content;
}

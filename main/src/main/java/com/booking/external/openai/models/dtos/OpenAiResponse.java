package com.booking.external.openai.models.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiResponse {
    private List<OpenAiChoice> choices;
}

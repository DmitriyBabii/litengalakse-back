package com.booking.external.openai.models.dtos;

import java.util.List;

public record OpenAiResponse(
        List<OpenAiChoice> choices
) {
}

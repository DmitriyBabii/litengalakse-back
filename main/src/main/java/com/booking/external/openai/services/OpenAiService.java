package com.booking.external.openai.services;

import com.booking.external.openai.models.dtos.OpenAiMessage;
import com.booking.external.openai.models.dtos.OpenAiRequest;
import com.booking.external.openai.models.dtos.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    public static final int MAX_TOKENS = 500;
    public static final double TEMPERATURE = .2;

    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.url}")
    private String url;
    @Value("${openai.api.key}")
    private String key;

    private final RestTemplate restTemplate;

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(key);
        return headers;
    }

    public OpenAiResponse sendRequest(OpenAiRequest requestBody) {
        HttpEntity<OpenAiRequest> openAiRequest = new HttpEntity<>(requestBody, getAuthHeaders());
        return restTemplate.postForObject(url, openAiRequest, OpenAiResponse.class);
    }

    public OpenAiRequest buildRequest() {
        return OpenAiRequest.builder()
                .model(model)
                .messages(List.of(
                        OpenAiMessage.builder()
                                .role("user")
                                .content("Tell me something interesting")
                                .build()
                ))
                .maxTokens(MAX_TOKENS)
                .temperature(TEMPERATURE)
                .build();
    }

    public OpenAiRequest buildRequest(List<OpenAiMessage> messages) {
        return OpenAiRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(MAX_TOKENS)
                .temperature(TEMPERATURE)
                .build();
    }
}

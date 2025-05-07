package com.booking.external.amadeus.services;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotel;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotelsResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusAiRoute;
import com.booking.external.amadeus.models.dtos.routes.AmadeusAiRoutesResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusRouteAdvice;
import com.booking.external.openai.models.dtos.OpenAiChoice;
import com.booking.external.openai.models.dtos.OpenAiMessage;
import com.booking.external.openai.models.dtos.OpenAiRequest;
import com.booking.external.openai.models.dtos.OpenAiResponse;
import com.booking.external.openai.services.OpenAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmadeusAiService {
    private static final String AI_SYSTEM_PROMPT = """
            You are traveler helper.
            You must help with finding places to stay.
            User will give you request and your job is to response with json only:
            {"cities":[{"cityCode":string,"description":string}]}
            cities: cities that you advise to visit, there could be many cities if its needed for user prompt (max count of cities is 5)
            description: you must describe why you choose it shortly
            Yor response always must be on english
            """;
    public static final String RADIUS = "5";
    public static final String RATINGS = "4,5";
    public static final String MESSAGE_ROLE_SYSTEM = "system";
    public static final String MESSAGE_ROLE_USER = "user";
    public static final int HOTEL_LIMIT = 10;

    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService;
    private final AmadeusService amadeusService;

    public List<AmadeusRouteAdvice> getAmadeusRouteAdvices(String prompt) {
        return getOpenAiRouteAdvices(prompt).getCities()
                .stream()
                .map(city -> {
                    HashMap<String, String> params = generateRouteAdvice(city);
                    AmadeusHotelsResponse hotelsByCity = amadeusService.findHotelsByCity(params);
                    List<AmadeusHotel> hotels = hotelsByCity.getData().stream().limit(HOTEL_LIMIT).toList();
                    return AmadeusRouteAdvice.builder()
                            .cityCode(city.getCityCode())
                            .description(city.getDescription())
                            .data(hotels)
                            .meta(hotelsByCity.getMeta())
                            .build();
                })
                .toList();
    }

    private static HashMap<String, String> generateRouteAdvice(AmadeusAiRoute city) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cityCode", city.getCityCode());
        params.put("radius", RADIUS);
        params.put("ratings", RATINGS);
        return params;
    }

    private AmadeusAiRoutesResponse getOpenAiRouteAdvices(String prompt) {
        OpenAiRequest openAiRequest = openAiService.buildRequest(getMessagesByPrompt(prompt));
        OpenAiResponse openAiResponse = openAiService.sendRequest(openAiRequest);
        String responseMessage = extractContent(openAiResponse);

        try {
            return objectMapper.readValue(responseMessage, AmadeusAiRoutesResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI response: {}", responseMessage, e);
            throw new IllegalArgumentException("Invalid AI response format", e);
        }
    }

    private static String extractContent(OpenAiResponse openAiResponse) {
        List<OpenAiChoice> choices = openAiResponse.getChoices();
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("OpenAI response does not contain any choices");
        }
        return choices.getFirst().getMessage().getContent();
    }

    private static List<OpenAiMessage> getMessagesByPrompt(String prompt) {
        return List.of(
                OpenAiMessage.builder()
                        .role(MESSAGE_ROLE_SYSTEM)
                        .content(AI_SYSTEM_PROMPT)
                        .build(),
                OpenAiMessage.builder()
                        .role(MESSAGE_ROLE_USER)
                        .content(prompt)
                        .build()
        );
    }
}
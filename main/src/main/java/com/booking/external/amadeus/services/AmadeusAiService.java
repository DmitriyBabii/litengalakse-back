package com.booking.external.amadeus.services;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotelsResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusAiRoutesResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusRouteAdvice;
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

    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService;
    private final AmadeusService amadeusService;

    public List<AmadeusRouteAdvice> findTravelRoutesByPrompt(String prompt) {
        OpenAiRequest openAiRequest = openAiService.buildRequest(getMessagesByPrompt(prompt));
        OpenAiResponse openAiResponse = openAiService.sendRequest(openAiRequest);
        String responseMessage = getContent(openAiResponse);

        log.info("Ai response message: {}", responseMessage);

        return getAmadeusRouteAdvices(responseMessage);
    }

    private List<AmadeusRouteAdvice> getAmadeusRouteAdvices(String responseMessage) {
        try {
            AmadeusAiRoutesResponse amadeusAiRoutesResponse = objectMapper.readValue(responseMessage, AmadeusAiRoutesResponse.class);

            return amadeusAiRoutesResponse.getCities().stream()
                    .map(city -> {
                        HashMap<String, String> params = new HashMap<>();

                        params.put("cityCode", city.getCityCode());
                        params.put("radius", "5");
                        params.put("ratings", "4,5");

                        AmadeusHotelsResponse hotelsByCity = amadeusService.findHotelsByCity(params);
                        return new AmadeusRouteAdvice(city.getCityCode(), hotelsByCity.getData(), hotelsByCity.getMeta());
                    })
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContent(OpenAiResponse openAiResponse) {
        return openAiResponse.getChoices().getFirst().getMessage().getContent();
    }

    private static List<OpenAiMessage> getMessagesByPrompt(String prompt) {
        return List.of(
                OpenAiMessage.builder()
                        .role("system")
                        .content(AI_SYSTEM_PROMPT)
                        .build(),
                OpenAiMessage.builder()
                        .role("user")
                        .content(prompt)
                        .build()
        );
    }
}

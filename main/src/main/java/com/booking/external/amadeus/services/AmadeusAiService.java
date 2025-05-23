package com.booking.external.amadeus.services;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotel;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotelsResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusAiRoute;
import com.booking.external.amadeus.models.dtos.routes.AmadeusAiRoutesResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusRouteAdvice;
import com.booking.external.amadeus.models.enums.Amenities;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmadeusAiService {
    private static final String AI_SYSTEM_PROMPT = String.format(
            """
                    You are traveler helper.
                    You must help with finding places to stay.
                    User will give you request and your job is to response with json only:
                    {"cities":[{"cityCode":string,"description":string,"amenities":string[]}]}
                    cities: cities that you advise to visit, there could be many cities if its needed for user prompt (max count of cities is 5)
                    cityCode: city code must be with standard of IATA only
                    description: you must describe why you choose it shortly
                    amenities: amenities you can add if its fits user query, amenities available:[%s]
                    Yor response always must be on english
                    """,
            Arrays.stream(Amenities.values())
                    .map(Amenities::name)
                    .collect(Collectors.joining(","))
    );
    public static final String RADIUS = "5";
    public static final String RATINGS = "4,5";
    public static final String MESSAGE_ROLE_SYSTEM = "system";
    public static final String MESSAGE_ROLE_USER = "user";
    public static final int HOTEL_LIMIT = 5;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService;
    private final AmadeusService amadeusService;

    public List<AmadeusRouteAdvice> getAmadeusRouteAdvices(String prompt) {
        List<CompletableFuture<Optional<AmadeusRouteAdvice>>> list = getOpenAiRouteAdvices(prompt).cities()
                .stream()
                .map(city -> CompletableFuture.supplyAsync(() -> {
                    try {
                        HashMap<String, String> params = generateRouteAdviceParams(city);
                        AmadeusHotelsResponse hotelsByCity = amadeusService.findHotelsByCity(params);
                        List<AmadeusHotel> hotels = hotelsByCity.data().stream().limit(HOTEL_LIMIT).toList();
                        return Optional.of(AmadeusRouteAdvice.builder()
                                .cityCode(city.cityCode())
                                .description(city.description())
                                .amenities(city.amenities())
                                .data(hotels)
                                .meta(hotelsByCity.meta())
                                .build());
                    } catch (Exception e) {
                        log.warn("Failed to get hotels for city: {}", city.cityCode(), e);
                    }
                    return Optional.<AmadeusRouteAdvice>empty();
                }))
                .toList();
        return list.stream()
                .map(CompletableFuture::join)
                .flatMap(Optional::stream)
                .toList();
    }

    private static HashMap<String, String> generateRouteAdviceParams(AmadeusAiRoute city) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cityCode", city.cityCode());
        params.put("radius", RADIUS);
        params.put("ratings", RATINGS);
        params.put("amenities", String.join(",", city.amenities()));
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
        List<OpenAiChoice> choices = openAiResponse.choices();
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("OpenAI response does not contain any choices");
        }
        return choices.getFirst().message().content();
    }

    private static List<OpenAiMessage> getMessagesByPrompt(String prompt) {
        return List.of(
                new OpenAiMessage(MESSAGE_ROLE_SYSTEM, AI_SYSTEM_PROMPT),
                new OpenAiMessage(MESSAGE_ROLE_USER, prompt)
        );
    }
}
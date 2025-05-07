package com.booking.controllers;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotelsResponse;
import com.booking.external.amadeus.models.dtos.routes.AmadeusRouteAdvice;
import com.booking.external.amadeus.services.AmadeusAiService;
import com.booking.external.amadeus.services.AmadeusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelsController {
    private final AmadeusService amadeusService;
    private final AmadeusAiService amadeusAiService;

    @PostMapping
    public AmadeusHotelsResponse getHotelsByParams(@RequestBody Map<String, String> params) {
        return amadeusService.findHotelsByCity(params);
    }

    @GetMapping
    public List<AmadeusRouteAdvice> getHotelsWithAi(@RequestParam String prompt) {
        return amadeusAiService.getAmadeusRouteAdvices(prompt);
    }
}

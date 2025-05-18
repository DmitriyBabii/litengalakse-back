package com.booking.controllers;

import com.booking.external.amadeus.models.dtos.routes.AmadeusRouteAdvice;
import com.booking.external.amadeus.services.AmadeusAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelsController {
    private final AmadeusAiService amadeusAiService;

    @PostMapping
    public List<AmadeusRouteAdvice> getHotelsWithAi(@RequestBody String prompt) {
        return amadeusAiService.getAmadeusRouteAdvices(prompt);
    }
}

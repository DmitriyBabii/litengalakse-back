package com.booking.controllers;

import com.booking.amadeus.services.AmadeusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelsController {
    private final AmadeusService amadeusService;

    @PostMapping
    public Map<String, String> getHotels(@RequestBody Map<String, String> params) {
        return amadeusService.findHotelsByCity(params);
    }
}

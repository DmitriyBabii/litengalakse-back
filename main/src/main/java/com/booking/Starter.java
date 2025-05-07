package com.booking;

import com.booking.external.amadeus.services.AmadeusService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class Starter implements CommandLineRunner {
    private final AmadeusService amadeusService;

    @Override
    public void run(String... args) throws Exception {
        HashMap<String, String> params = new HashMap<>();

        params.put("cityCode", "BLR");
        params.put("radius", "10");
        params.put("ratings", "4,5");

        amadeusService.findHotelsByCity(params);
    }
}

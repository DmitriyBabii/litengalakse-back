package com.booking.external.amadeus.models.dtos.routes;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotel;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusMeta;

import java.util.List;

public record AmadeusRouteAdvice(
        String cityCode,
        String description,
        List<AmadeusHotel> data,
        AmadeusMeta meta
) {
}

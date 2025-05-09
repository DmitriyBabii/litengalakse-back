package com.booking.external.amadeus.models.dtos.routes;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotel;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusMeta;
import lombok.Builder;

import java.util.List;

@Builder
public record AmadeusRouteAdvice(
        String cityCode,
        String description,
        List<AmadeusHotel> data,
        List<String> amenities,
        AmadeusMeta meta
) {
}

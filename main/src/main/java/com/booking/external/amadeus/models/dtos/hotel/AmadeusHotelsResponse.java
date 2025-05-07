package com.booking.external.amadeus.models.dtos.hotel;

import java.util.List;


public record AmadeusHotelsResponse(
        List<AmadeusHotel> data,
        AmadeusMeta meta
) {
}

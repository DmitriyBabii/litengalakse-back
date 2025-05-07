package com.booking.external.amadeus.models.dtos.hotel;

import java.time.LocalDateTime;

public record AmadeusHotel(
        String chainCode,
        String iataCode,
        Integer dupeId,
        String name,
        String hotelId,
        AmadeusGeoCode geoCode,
        AmadeusDistance distance,
        Integer rating,
        LocalDateTime lastUpdate
) {
}

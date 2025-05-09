package com.booking.external.amadeus.models.dtos.routes;

import java.util.List;

public record AmadeusAiRoute(
        String cityCode,
        String description,
        List<String> amenities
) {
}

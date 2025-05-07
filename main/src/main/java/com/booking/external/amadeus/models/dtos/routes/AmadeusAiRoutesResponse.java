package com.booking.external.amadeus.models.dtos.routes;

import java.util.List;

public record AmadeusAiRoutesResponse(
        List<AmadeusAiRoute> cities
) {
}

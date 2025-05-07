package com.booking.external.amadeus.models.dtos.routes;

import lombok.Getter;

import java.util.List;

@Getter
public class AmadeusAiRoutesResponse {
    private List<AmadeusAiRoute> cities;
}

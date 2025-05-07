package com.booking.external.amadeus.models.dtos.hotel;

import lombok.Getter;

import java.util.List;


@Getter
public class AmadeusHotelsResponse {
    private List<AmadeusHotel> data;
    private AmadeusMeta meta;
}

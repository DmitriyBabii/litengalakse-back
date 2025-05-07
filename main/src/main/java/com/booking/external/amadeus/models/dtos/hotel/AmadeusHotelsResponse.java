package com.booking.external.amadeus.models.dtos.hotel;

import lombok.Getter;

import java.util.List;


// TODO think about class name
@Getter
public class AmadeusHotelsResponse {
    private List<AmadeusHotel> data;
    private AmadeusMeta meta;
}

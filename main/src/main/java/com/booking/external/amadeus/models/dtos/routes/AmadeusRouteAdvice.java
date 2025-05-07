package com.booking.external.amadeus.models.dtos.routes;

import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotel;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AmadeusRouteAdvice {
    private String cityCode;
    private List<AmadeusHotel> data;
    private AmadeusMeta meta;
}

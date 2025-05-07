package com.booking.external.amadeus.models.dtos.hotel;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AmadeusHotel {
    private String chainCode;
    private String iataCode;
    private Integer dupeId;
    private String name;
    private String hotelId;
    private AmadeusGeoCode geoCode;
    private AmadeusDistance distance;
    private Integer rating;
    private LocalDateTime lastUpdate;
}

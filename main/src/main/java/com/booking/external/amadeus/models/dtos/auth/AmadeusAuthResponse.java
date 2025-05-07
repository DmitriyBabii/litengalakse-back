package com.booking.external.amadeus.models.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AmadeusAuthResponse(
        String type,
        String username,
        @JsonProperty("application_name") String applicationName,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Integer expiresIn,
        String state,
        String scope
) {
}

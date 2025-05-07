package com.booking.external.amadeus.services;

import com.booking.external.amadeus.models.dtos.auth.AmadeusAuthResponse;
import com.booking.external.amadeus.models.dtos.hotel.AmadeusHotelsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmadeusService {
    public static final String REDIS_KEY_PREFIX = "auth:";
    public static final String TOKEN_KEY = REDIS_KEY_PREFIX + "token";
    public static final long TOKEN_EXPIRES_BUFFER = 60L;
    public static final long TOKEN_EXPIRES_MIN = 5L;


    @Value("${amadeus.api.key}")
    private String apiKey;
    @Value("${amadeus.api.secret}")
    private String apiSecret;

    @Value("${amadeus.api.url.authorize}")
    private String authorizeUrl;

    @Value("${amadeus.api.url.by.city}")
    private String searchUrlByCity;


    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private AmadeusAuthResponse authorize() {
        HttpHeaders headers = getAuthHeaders();
        MultiValueMap<String, String> authorizeRequestBody = getAuthorizeRequestBody();
        HttpEntity<MultiValueMap<String, String>> authorizeRequest = new HttpEntity<>(authorizeRequestBody, headers);
        AmadeusAuthResponse amadeusAuthResponse = restTemplate.postForObject(authorizeUrl, authorizeRequest, AmadeusAuthResponse.class);

        if (amadeusAuthResponse == null) {
            throw new RestClientException("Empty body from Amadeus authorization response");
        }

        log.info("Successful authorization with token: {}", amadeusAuthResponse.getAccessToken());
        return amadeusAuthResponse;
    }

    private static HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> getAuthorizeRequestBody() {
        MultiValueMap<String, String> authorizeRequestBody = new LinkedMultiValueMap<>();
        authorizeRequestBody.add("grant_type", "client_credentials");
        authorizeRequestBody.add("client_id", apiKey);
        authorizeRequestBody.add("client_secret", apiSecret);
        return authorizeRequestBody;
    }

    private String getAccessToken() {
        String token = redisTemplate.opsForValue().get(TOKEN_KEY);

        if (token != null) {
            return token;
        }

        AmadeusAuthResponse response = authorize();

        long ttl = Math.max(response.getExpiresIn() - TOKEN_EXPIRES_BUFFER, TOKEN_EXPIRES_MIN);
        redisTemplate.opsForValue().set(TOKEN_KEY, response.getAccessToken(), Duration.ofSeconds(ttl));

        return response.getAccessToken();
    }

    public AmadeusHotelsResponse findHotelsByCity(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(searchUrlByCity);
        params.forEach(builder::queryParam);

        String url = builder.build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                AmadeusHotelsResponse.class
        ).getBody();
    }
}

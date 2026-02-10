package com.zkAttendance.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

@Service
public class ZohoTokenService {

    @Value("${zoho.client.id}")
    private String clientId;

    @Value("${zoho.client.secret}")
    private String clientSecret;

    @Value("${zoho.refresh.token}")
    private String refreshToken;

    @Value("${zoho.token.url}")
    private String tokenUrl;

    private String accessToken;
    private Instant expiryTime;

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiryTime)) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private void refreshAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        Map<String, Object> response =
                restTemplate.postForObject(tokenUrl, request, Map.class);

        this.accessToken = (String) response.get("access_token");
        int expiresIn = (Integer) response.get("expires_in");
        this.expiryTime = Instant.now().plusSeconds(expiresIn - 60);
    }
}
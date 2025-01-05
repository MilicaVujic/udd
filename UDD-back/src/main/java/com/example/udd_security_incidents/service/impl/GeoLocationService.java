package com.example.udd_security_incidents.service.impl;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.arnx.jsonic.JSONException;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
@Slf4j
public class GeoLocationService {
    public GeoLocationService(){}
    private final String accessToken="pk.2b070ee85ad646f7b1d94b649d1d7267";
    private final RestTemplate restTemplate=new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeoLocation getGeoPointForCity(String cityName) {
        String url = String.format("https://us1.locationiq.com/v1/search.php?key=%s&q=%s&format=json", accessToken, cityName);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // Parsiramo JSON odgovor
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                // Uzimamo prvi objekat iz niza
                JsonNode firstElement = rootNode.get(0);

                double latitude = firstElement.get("lat").asDouble();
                double longitude = firstElement.get("lon").asDouble();

                return createGeoLocation(latitude,longitude);
            } catch (Exception e) {
                log.error("Error parsing LocationIQ response: {}", e.getMessage());
            }
        }
        return null;
    }
    public GeoPoint getGeoPointForAddress(String address) {
        String url = String.format("%s?key=%s&q=%s&format=json", "https://us1.locationiq.com/v1/search.php", accessToken, address);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode firstElement = rootNode.get(0);

                double latitude = firstElement.get("lat").asDouble();
                double longitude = firstElement.get("lon").asDouble();

                return new GeoPoint(latitude,longitude);
            }
        } catch (Exception e) {
            System.err.println("Error while geocoding address: " + e.getMessage());
        }
        return null;
    }
    private GeoLocation createGeoLocation(double latitude, double longitude) {
        return GeoLocation.of(builder -> builder
                .latlon(latlon -> latlon
                        .lat(latitude)
                        .lon(longitude)
                )
        );
    }
}

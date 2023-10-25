package ru.practicum.stats.client;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHitDto;

@Service
@Log
public class HitClient {

    private static final String API_PREFIX = "/hit";
    private final RestTemplate restTemplate;

    //${stats.service.url}
    public HitClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {

        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<EndpointHitDto> addHit(EndpointHitDto endpointHitDto) {

        ResponseEntity<EndpointHitDto> response = restTemplate.postForEntity(
                "", endpointHitDto, EndpointHitDto.class);
        return response;
    }
}

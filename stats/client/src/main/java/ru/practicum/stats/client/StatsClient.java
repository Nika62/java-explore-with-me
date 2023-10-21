package ru.practicum.stats.client;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.StatsDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

@Service
@Log
public class StatsClient {

    private static final String API_PREFIX = "/stats";

    public final RestTemplate restTemplate;

    public StatsClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<StatsDto> getStats(String start, String end, Optional<String[]> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        var u = uris.get();
        try {
            log.info("GET uri" + u);
            ResponseEntity<StatsDto[]> response = restTemplate.exchange("?start={start}&end={end}&uris={uris}&unique={unique}", GET, null, StatsDto[].class, parameters);
            log.info("RESPONSE   " + response.toString());
            return Arrays.asList(response.getBody());
        } catch (HttpStatusCodeException e) {
            new RuntimeException(e.getMessage());
        }
        return null;
    }
}

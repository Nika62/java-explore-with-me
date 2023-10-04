package ru.practicum;


import java.util.List;
import java.util.Optional;

public interface StatsService {

    EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto);

    List<StatDto> getState(String start, String end, Optional<String[]> uris, Boolean unique);
}

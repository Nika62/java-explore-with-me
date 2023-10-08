package ru.practicum.stats.service;


import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.StatsDto;

import java.util.List;
import java.util.Optional;

@Service
public interface StatsService {

    EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto);

    List<StatsDto> getState(String start, String end, Optional<String[]> uris, Boolean unique);
}

package ru.practicum.impl;

import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.EndpointHitMapper;
import ru.practicum.StatsRepository;
import ru.practicum.StatsService;
import ru.practicum.StatDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository stateRepository;
    private final EndpointHitMapper mapper;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit returnEndpointHit = stateRepository.save(mapper.convertEndpointHitDtoToEndpointHit(endpointHitDto));
        System.out.println(returnEndpointHit.getId());

        EndpointHitDto d =  mapper.convertEndpointHitToEndpointHitDto(returnEndpointHit);
        System.out.println(d.getId());
        return d;
    }

    @Override
    public List<StatDto> getState(String start, String end, Optional<String[]> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, format);
        LocalDateTime endTime = LocalDateTime.parse(end, format);
        if (uris.isPresent()) {
            if (unique) {
            return stateRepository.getStatsByAllParameter(startTime, endTime, uris.get());
            } else {
                return ;
            }
        }
        if (unique) {
            return stateRepository.getStatsByDateTimeAndDistinctIp(startTime,endTime);
        }
        return stateRepository.getStatsByDateTime(startTime,endTime);
    }
}

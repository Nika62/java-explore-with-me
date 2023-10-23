package ru.practicum.stats.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.service.EndpointHitMapper;
import ru.practicum.stats.service.StatsRepository;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.service.model.EndpointHit;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private final EndpointHitMapper mapper;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit returnEndpointHit = statsRepository.save(mapper.convertEndpointHitDtoToEndpointHit(endpointHitDto));
        return mapper.convertEndpointHitToEndpointHitDto(returnEndpointHit);
    }

    @Override
    public List<StatsDto> getState(String start, String end, Optional<String[]> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, format);
        LocalDateTime endTime = LocalDateTime.parse(end, format);
        compareDate(startTime, endTime);
        if (uris.isPresent()) {
            return getStateByUris(startTime, endTime, uris, unique);
        }
        return getStateNoUris(startTime, endTime, unique);
    }

    private List<StatsDto> getStateByUris(LocalDateTime startTime, LocalDateTime endTime, Optional<String[]> uris, Boolean unique) {
        List<String> uriList = Arrays.asList(uris.get());
        if (unique) {
            return statsRepository.getStatsByAllParameterAndUnique(startTime, endTime, uriList);
        } else {
            return statsRepository.getStatsByAllParameterNoUnique(startTime, endTime, uriList);
        }
    }

    private List<StatsDto> getStateNoUris(LocalDateTime startTime, LocalDateTime endTime, Boolean unique) {
        if (unique) {
            return statsRepository.getStatsByDateTimeAndUnique(startTime, endTime);
        }
        return statsRepository.getStatsByDateTimeNoUnique(startTime, endTime);
    }

    private void compareDate(LocalDateTime dateMastBefore, LocalDateTime dateMastAfter) {
        if (dateMastBefore.isAfter(dateMastAfter)) {
            throw new ValidationException("The start should be before the end");
        }
    }
}

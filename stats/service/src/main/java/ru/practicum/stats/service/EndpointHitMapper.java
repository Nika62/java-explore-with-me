package ru.practicum.stats.service;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EndpointHitMapper {

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHit convertEndpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto) {
        if (endpointHitDto == null) {
            return null;
        }
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setCreationTime(LocalDateTime.parse(endpointHitDto.getTimestamp(), format));

        return endpointHit;
    }

    public EndpointHitDto convertEndpointHitToEndpointHitDto(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setId(endpointHit.getId());
        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setUri(endpointHit.getUri());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setTimestamp(endpointHit.getCreationTime().format(format));

        return endpointHitDto;
    }

}

package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.StatsDto;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto addEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {

       return statsService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) Optional <String[]> uris,
                                   @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getState(start,end,uris, unique);
    }

}

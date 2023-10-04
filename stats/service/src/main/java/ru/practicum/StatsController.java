package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.impl.StatsServiceImpl;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsServiceImpl statsService;

    @PostMapping("/hit")
    public EndpointHitDto addEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {

        EndpointHitDto ed =  statsService.addEndpointHit(endpointHitDto);
        System.out.println(ed.getId());
        return ed;
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam String start,
                                         @RequestParam String end,
                                         @RequestParam(required = false) Optional <String[]> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getState(start,end,uris, unique);
    }

}

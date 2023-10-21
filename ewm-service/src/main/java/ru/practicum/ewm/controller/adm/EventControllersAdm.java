package ru.practicum.ewm.controller.adm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllersAdm {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId, @RequestBody NewEventDto newEventDto, HttpServletRequest request) {
        return eventService.updateEventByAdmin(eventId, newEventDto, request);
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) Optional<Long[]> users, @RequestParam Optional<String[]> states,
                                        @RequestParam(required = false) Optional<Long[]> categories, @RequestParam(required = false) Optional<String> rangeStart,
                                        @RequestParam(required = false) Optional<String> rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}

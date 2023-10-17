package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@RestController
public class EventControllerPriv {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {

        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventByIdAndInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId, @RequestBody NewEventDto newEventDto) {
        return eventService.updateEventByUser(userId, eventId, newEventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto getUserEventRequests(@PathVariable long userId, @PathVariable long eventId, @RequestBody EventRequestStatusUpdateRequestDto body) {
        return requestService.reviewEventRequests(userId, eventId, body);
    }
}

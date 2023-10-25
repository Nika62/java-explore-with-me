package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.enums.EventSortParameter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface EventService {

    EventFullDto createEvent(long userId, NewEventDto newEventDto);

    List<EventFullDto> getEventsByUserId(long userId, int from, int size);

    EventFullDto getEventByIdAndInitiator(long userId, long eventId);

    EventFullDto updateEventByUser(long userId, long eventId, NewEventDto newEventDto);

    EventFullDto updateEventByAdmin(long eventId, NewEventDto newEventDto);

    List<EventFullDto> getEventsForAdmin(Optional<Long[]> users, Optional<String[]> states, Optional<Long[]> categories,
                                         Optional<String> rangeStart, Optional<String> rangeEnd, int from, int size);

    List<EventFullDto> getEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid, Optional<String> rangeStart,
                                 Optional<String> rangeEnd, Boolean onlyAvailable, Optional<EventSortParameter> sort, int from, int size, HttpServletRequest request);

    EventFullDto getEventById(long id, HttpServletRequest request);
}

package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;

public interface EventService {

    EventFullDto createEvent(long userId, NewEventDto newEventDto);
}

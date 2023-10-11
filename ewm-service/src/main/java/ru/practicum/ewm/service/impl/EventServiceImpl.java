package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.EventService;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventMapper mapper;

    @Override
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        Event event = mapper.convertNewEventDtoToEvent(newEventDto);
        event.setInitiator(new User(userId));
        return mapper.convertEventToEventFullDto(eventRepository.save(event));
    }
}

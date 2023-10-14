package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.enums.PublicationStatus;

import java.util.Objects;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event convertNewEventDtoToEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(new Category(newEventDto.getCategory()));
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(convertToDateTime(newEventDto.getEventDate()));
        event.setLocationLat(newEventDto.getLocation().getLat());
        event.setLocationLon(newEventDto.getLocation().getLon());
        event.setPaid(Objects.nonNull(newEventDto.getPaid()) ? newEventDto.getPaid() : false);
        event.setParticipantLimit(Objects.nonNull(newEventDto.getParticipantLimit()) ? newEventDto.getParticipantLimit() : 0);
        event.setRequestModeration(Objects.nonNull(newEventDto.getRequestModeration()) ? newEventDto.getRequestModeration() : true);
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public EventFullDto convertEventToEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryMapper.convertCategoryToCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(convertToString(event.getEventDate()));
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(userMapper.convertUserToUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLocationLat(), event.getLocationLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn((Objects.nonNull(event.getPublishedOn()) ? convertToString(event.getPublishedOn()) : null));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(PublicationStatus.valueOf(event.getState()));
        eventFullDto.setTitle(event.getTitle());

        return eventFullDto;
    }
}

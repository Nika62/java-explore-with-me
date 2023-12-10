package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.SearchFilterEvent;
import ru.practicum.ewm.dto.event.SearchFilterEventAdm;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventSortParameter;
import ru.practicum.ewm.model.enums.StateAction;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.model.exception.ValidationException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.stats.client.HitClient;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;
import static ru.practicum.ewm.model.enums.EventSortParameter.VIEWS;
import static ru.practicum.ewm.model.enums.PublicationStatus.CANCELED;
import static ru.practicum.ewm.model.enums.PublicationStatus.PENDING;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;
import static ru.practicum.ewm.model.enums.StateAction.CANCEL_REVIEW;
import static ru.practicum.ewm.model.enums.StateAction.PUBLISH_EVENT;
import static ru.practicum.ewm.model.enums.StateAction.REJECT_EVENT;
import static ru.practicum.ewm.model.enums.StateAction.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final CommentMapper commentMapper;
    private final EventSpecification eventSpecification;
    private final HitClient hitClient;
    private final StatsClient statsClient;


    @Override
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        LocalDateTime createdDate = LocalDateTime.now();
        compareDate(createdDate.plusHours(2), convertToDateTime(newEventDto.getEventDate()),
                "Field: eventDate. Error: must contain a date that has not yet arrived. Value: " + newEventDto.getEventDate());
        Event event = mapper.convertNewEventDtoToEvent(newEventDto);
        event.setCreatedOn(createdDate);
        event.setState(PENDING.name());
        event.setInitiator(new User(userId));
        return mapper.convertEventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsByUserId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.getAllEventByInitiatorId(userId, pageRequest).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdAndInitiator(long userId, long eventId) {
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(getEventByIdOrException(userId, eventId));
        setView(eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, NewEventDto newEventDto) {
        Event event = getEventByIdOrException(userId, eventId);
        checkStateEvent(event);
        setEventParameters(event, newEventDto);
        if (Objects.nonNull(newEventDto.getStateAction())) {
            setStateEvent(newEventDto.getStateAction(), event);
        }
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(eventRepository.save(event));
        setView(eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, NewEventDto newEventDto) {
        Event event = getEventByIdOrException(eventId);
        checkStateEvent(event);
        setEventParameters(event, newEventDto);
        LocalDateTime publishedDate = LocalDateTime.now();
        StateAction stateAction = newEventDto.getStateAction();

        if (Objects.nonNull(stateAction)) {
            if (stateAction.equals(PUBLISH_EVENT)) {
                compareDate(publishedDate.plusHours(1), event.getEventDate(),
                        "The publication date must be an hour or more earlier than the event date");
                event.setPublishedOn(publishedDate);
            }
            setStateEvent(stateAction, event);
        }
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(eventRepository.save(event));
        setView(eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Optional<Long[]> users, Optional<String[]> states, Optional<Long[]> categories,
                                                Optional<String> rangeStart, Optional<String> rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (users.isEmpty() && states.isEmpty() && categories.isEmpty() && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
            return eventRepository.findAll(pageRequest).stream().map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
        }
        SearchFilterEventAdm searchFilterEventAdm = new SearchFilterEventAdm(users, states, categories, rangeStart, rangeEnd);
        List<Specification<Event>> specifications = eventSpecification.searchFilterSpecificationsAdm(searchFilterEventAdm);
        return eventRepository.findAll(specifications.stream().reduce(Specification::and).get(), pageRequest).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid, Optional<String> rangeStart,
                                        Optional<String> rangeEnd, Boolean onlyAvailable,
                                        Optional<EventSortParameter> sort, int from, int size, HttpServletRequest request) {
        text = text.isPresent() ? Optional.of(text.get().toLowerCase()) : Optional.empty();

        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            compareDate(convertToDateTime(rangeStart.get()), convertToDateTime(rangeEnd.get()), "The rangeStart should be before the rangeEnd");
            }
        SearchFilterEvent filter = new SearchFilterEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        List<Specification<Event>> specifications = eventSpecification.searchFilterToSpecifications(filter);
        addHit(request);
        if (sort.isPresent()) {
            return getEventsByFilterAndSort(specifications, sort, from, size);
        }
        return getEventsByFilter(specifications, from, size);
    }

    @Override
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        addHit(request);
        Event event = eventRepository.getEventByIdAndState(id, PUBLISHED.name()).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.",
                        "Event with id=" + id + " was not found", LocalDateTime.now()));
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(event);
        eventFullDto.setComments(event.getComments().stream().map(commentMapper::convertCommentToCommentDto).collect(Collectors.toList()));
        setView(eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsInPlace(float latitude, float longitude, int radius) {
        return eventRepository.getEventsInPlace(latitude, longitude, radius).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsByFilterAndSort(List<Specification<Event>> specifications, Optional<EventSortParameter> sort, int from, int size) {
        String sortValue = sort.get().equals(VIEWS) ? "views" : "eventDate";
        Pageable sortedPageable = PageRequest.of(from / size, size, Sort.by(sortValue));
        return eventRepository.findAll(specifications.stream().reduce(Specification::and).get(), sortedPageable).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsByFilter(List<Specification<Event>> specifications, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAll(specifications.stream().reduce(Specification::and).get(), pageRequest).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }


    private void compareDate(LocalDateTime dateMastBefore, LocalDateTime dateMastAfter, String errorMessage) {
        if (dateMastBefore.isAfter(dateMastAfter)) {
            throw new ValidationException("Incorrectly made request.",
                    errorMessage, LocalDateTime.now());
        }
    }

    private Event getEventByIdOrException(long userId, long eventId) {
        return eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.",
                        "Event with id=" + eventId + " was not found", LocalDateTime.now())
        );
    }

    private void checkStateEvent(Event event) {
        if (event.getState().equals(PUBLISHED.name())) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                    "Unable to update a published event.", LocalDateTime.now());
        }
    }

    private void setStateEvent(StateAction stateAction, Event event) {
        if (stateAction.equals(SEND_TO_REVIEW)) {
            event.setState(PENDING.name());
        } else if (stateAction.equals(PUBLISH_EVENT)) {
            if (event.getState().equals(CANCELED.name())) {
                throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                        "Unable to publish a canceled event.", LocalDateTime.now());
            }
            event.setState(PUBLISHED.name());
        } else if (stateAction.equals(REJECT_EVENT) || stateAction.equals(CANCEL_REVIEW)) {
            event.setState(CANCELED.name());
        }
    }

    private void setEventParameters(Event event, NewEventDto newEventDto) {
        setAnnotation(event, newEventDto);
        setCategory(event, newEventDto);
        setDescription(event, newEventDto);
        setEventDate(event, newEventDto);
        setEventLocation(event, newEventDto);
        setPaid(event, newEventDto);
        setParticipantLimit(event, newEventDto);
        setRequestModeration(event, newEventDto);
        setTitle(event, newEventDto);
    }

    private Event getEventByIdOrException(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.",
                        "Event with id=" + eventId + " was not found", LocalDateTime.now())
        );
    }

    private void setAnnotation(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getAnnotation())) {
            String annotation = newEventDto.getAnnotation();
            if (annotation.length() <= 2000 && annotation.length() >= 20
                    && !annotation.isBlank()) {
                event.setAnnotation(newEventDto.getAnnotation());
            } else {
                throw new ValidationException("Incorrectly made request.", "Annotation length should be from 20 to 2000", LocalDateTime.now());
            }
        }
    }

    private void setCategory(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getCategory())) {
            event.setCategory(new Category(newEventDto.getCategory()));
        }
    }

    private void setDescription(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getDescription())) {
            String descr = newEventDto.getDescription();
            if (descr.length() <= 7000 && descr.length() >= 20
                    && !descr.isBlank()) {
                event.setDescription(newEventDto.getDescription());
            } else {
                throw new ValidationException("Incorrectly made request.", "Description length should be from 20 to 7000", LocalDateTime.now());
            }
        }
    }

    private void setEventDate(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getEventDate())) {
            LocalDateTime eventDate = convertToDateTime(newEventDto.getEventDate());
            compareDate(LocalDateTime.now().plusHours(2), eventDate,
                    "Field: eventDate. Error: must save dates that have not yet arrived. Value: " + eventDate);
            event.setEventDate(eventDate);
        }
    }

    private void setEventLocation(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getLocation())) {
            event.setLocationLat(newEventDto.getLocation().getLat());
            event.setLocationLon(newEventDto.getLocation().getLon());
        }
    }

    private void setPaid(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getPaid())) {
            event.setPaid(newEventDto.getPaid());
        }
    }

    private void setParticipantLimit(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getParticipantLimit())) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
    }

    private void setRequestModeration(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getRequestModeration())) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
    }

    private void setTitle(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getTitle())) {
            String title = newEventDto.getTitle();
            if (title.length() <= 120 && title.length() >= 3
                    && !title.isBlank()) {
                event.setTitle(newEventDto.getTitle());
            } else {
                throw new ValidationException("Incorrectly made request.", "Title length should be from 3 to 120", LocalDateTime.now());
            }
        }
    }

    private void addHit(HttpServletRequest request) {
        hitClient.addHit(new EndpointHitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                convertToString(LocalDateTime.now())));
    }

    private void setView(EventFullDto eventFullDto) {
        String[] uri = new String[1];
        uri[0] = "/events/" + eventFullDto.getId();
        List<StatsDto> statsResponse = statsClient.getStats(eventFullDto.getCreatedOn(), eventFullDto.getEventDate(), uri, true);
        if (Objects.nonNull(statsResponse) && statsResponse.size() > 0) {
            eventFullDto.setViews(statsResponse.get(0).getHits());
        }
    }

}

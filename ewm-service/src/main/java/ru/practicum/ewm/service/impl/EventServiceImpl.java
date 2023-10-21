package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.SearchFilterEvent;
import ru.practicum.ewm.dto.event.SearchFilterEventAdm;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;
import static ru.practicum.ewm.model.enums.EventSortParameter.VIEWS;
import static ru.practicum.ewm.model.enums.PublicationStatus.*;
import static ru.practicum.ewm.model.enums.StateAction.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final EventSpecification eventSpecification;
    private final HitClient hitClient;
    private final StatsClient statsClient;


    @Override
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        LocalDateTime createdDate = LocalDateTime.now();
        compareDate(createdDate.plusHours(2), convertToDateTime(newEventDto.getEventDate()),
                "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + newEventDto.getEventDate());
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
    public EventFullDto getEventByIdAndInitiator(long userId, long eventId, HttpServletRequest request) {
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(getEventByIdOrException(userId, eventId));
        setView(eventFullDto, request);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, NewEventDto newEventDto, HttpServletRequest request) {
        Event event = getEventByIdOrException(userId, eventId);
        checkStateEvent(event);
        setEventParameters(event, newEventDto);
        if (Objects.nonNull(newEventDto.getStateAction())) {
            setStateEvent(newEventDto.getStateAction(), event);
        }
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(eventRepository.save(event));
        setView(eventFullDto, request);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, NewEventDto newEventDto, HttpServletRequest request) {
        Event event = getEventByIdOrException(eventId);
        checkStateEvent(event);
        setEventParameters(event, newEventDto);

        LocalDateTime publishedDate = LocalDateTime.now();
        StateAction stateAction = newEventDto.getStateAction();

        if (Objects.nonNull(stateAction)) {
            if (stateAction.equals(PUBLISH_EVENT)) {
                compareDate(publishedDate.plusHours(1), event.getEventDate(),
                        "Cannot publish the event because it's not in the right state: PUBLISHED");
                event.setPublishedOn(publishedDate);
            }
            setStateEvent(stateAction, event);
        }

        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(eventRepository.save(event));
        setView(eventFullDto, request);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Optional<Long[]> users, Optional<String[]> states, Optional<Long[]> categories,
                                                Optional<String> rangeStart, Optional<String> rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Optional<SearchFilterEventAdm> searchFilterEventAdm = Optional.ofNullable(new SearchFilterEventAdm(Arrays.asList(users.get()),
                Arrays.asList(states.get()), Arrays.asList(categories.get()), rangeStart.get(), rangeEnd.get()));
        if (searchFilterEventAdm.isPresent()) {
            List<Specification<Event>> specifications = eventSpecification.searchFilterSpecificationsAdm(searchFilterEventAdm.get());
            return eventRepository.findAll(specifications.stream().reduce(Specification::and).get(), pageRequest).stream()
                    .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
        }
        return eventRepository.findAll(pageRequest).stream().map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid, Optional<String> rangeStart,
                                        Optional<String> rangeEnd, Boolean onlyAvailable,
                                        Optional<EventSortParameter> sort, int from, int size, HttpServletRequest request) {
        String searchText = Objects.nonNull(text.get()) ? text.get().toLowerCase() : null;
        SearchFilterEvent filter = new SearchFilterEvent(searchText, Arrays.asList(categories.get()), paid.get(), rangeStart.get(), rangeEnd.get(), onlyAvailable);
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
        EventFullDto eventFullDto = mapper.convertEventToEventFullDto(getEventByIdOrException(id));
        setView(eventFullDto, request);
        return eventFullDto;
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
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                    errorMessage, LocalDateTime.now());
        }
    }

    private Event getEventByIdOrException(long userId, long eventId) {
        return eventRepository.getEventByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ObjectNotFoundException("Integrity constraint has been violated.",
                        "Event with id=" + eventId + "was not found", LocalDateTime.now())
        );
    }

    private void checkStateEvent(Event event) {
        if (event.getState().equals(PUBLISHED.name())) {
            getEventNotSatisfyRulesException("Only pending or canceled events can be changed");
        }
    }

    private void setStateEvent(StateAction stateAction, Event event) {
        if (stateAction.equals(SEND_TO_REVIEW)) {
            event.setState(PENDING.name());
        } else if (stateAction.equals(PUBLISH_EVENT)) {
            if (event.getState().equals(CANCELED.name()) ||
                    event.getState().equals(PUBLISHED.name())) {
                getEventNotSatisfyRulesException("Only pending or canceled events can be changed");
            }
            event.setState(PUBLISHED.name());
        } else if (stateAction.equals(REJECT_EVENT)) {
            event.setState(CANCELED.name());
        }
    }

    private void setEventParameters(Event event, NewEventDto newEventDto) {
        setAnnotation(event, newEventDto);
        setCategory(event, newEventDto);
        setDescription(event, newEventDto);
        setEventDate(event, newEventDto);
        setLocation(event, newEventDto);
        setPaid(event, newEventDto);
        setParticipantLimit(event, newEventDto);
        setRequestModeration(event, newEventDto);
        setTitle(event, newEventDto);
    }

    private Event getEventByIdOrException(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Integrity constraint has been violated.",
                        "Event with id=" + eventId + "was not found", LocalDateTime.now())
        );
    }

    private ObjectNotSatisfyRulesException getEventNotSatisfyRulesException(String message) {
        throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                message,
                LocalDateTime.now());
    }

    private void setAnnotation(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getAnnotation())) {
            String annotation = newEventDto.getAnnotation();
            if (annotation.length() <= 2000 && annotation.length() >= 20
                    && !annotation.isBlank()) {
                event.setAnnotation(newEventDto.getAnnotation());
            } else {
                getValidationException();
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
                getValidationException();
            }
        }
    }

    private void setEventDate(Event event, NewEventDto newEventDto) {
        if (Objects.nonNull(newEventDto.getEventDate())) {
            LocalDateTime eventDate = convertToDateTime(newEventDto.getEventDate());
            compareDate(LocalDateTime.now().plusHours(2), eventDate,
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate);
            event.setEventDate(eventDate);
        }
    }

    private void setLocation(Event event, NewEventDto newEventDto) {
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
            if (title.length() <= 130 && title.length() >= 3
                    && !title.isBlank()) {
                event.setTitle(newEventDto.getTitle());
            } else {
                getValidationException();
            }
        }
    }

    private ValidationException getValidationException() {
        throw new ValidationException("Incorrectly made request.", "Event must not be published", LocalDateTime.now());
    }

    private Page<Event> selectMethodByParameters(Optional<Long[]> users, String[] states, Optional<Long[]> categories,
                                                 LocalDateTime start, LocalDateTime end, PageRequest pageRequest) {
        if (users.isPresent() && !categories.isPresent()) {
            return eventRepository.getEventByUsesStatesAndEventDate(Arrays.asList(users.get()), Arrays.asList(states), start, end, pageRequest);
        } else if (!users.isPresent() && categories.isPresent()) {
            return eventRepository.getEventByStatesCategoriesAndEventDate(Arrays.asList(states), Arrays.asList(categories.get()), start, end, pageRequest);
        }
        return eventRepository.getEventByUsesStatesCategoriesAndEventDate(Arrays.asList(users.get()), Arrays.asList(states), Arrays.asList(categories.get()), start, end, pageRequest);
    }

    private void addHit(HttpServletRequest request) {
        hitClient.addHit(new EndpointHitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                convertToString(LocalDateTime.now())));
    }

    private void setView(EventFullDto eventFullDto, HttpServletRequest request) {
        String[] uri = new String[1];
        uri[0] = request.getRequestURI();
        List<StatsDto> statsResponse = statsClient.getStats(eventFullDto.getPublishedOn(), eventFullDto.getEventDate(), Optional.of(uri), true);
        if (Objects.nonNull(statsResponse) && statsResponse.size() > 0) {
            eventFullDto.setViews(statsResponse.get(0).getHits());
        }
    }

}

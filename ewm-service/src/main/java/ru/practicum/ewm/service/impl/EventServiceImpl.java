package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.StateAction;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.model.exception.ValidationException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.model.enums.PublicationStatus.CANCELED;
import static ru.practicum.ewm.model.enums.PublicationStatus.PENDING;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;
import static ru.practicum.ewm.model.enums.StateAction.PUBLISH_EVENT;
import static ru.practicum.ewm.model.enums.StateAction.REJECT_EVENT;
import static ru.practicum.ewm.model.enums.StateAction.SEND_TO_REVIEW;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper mapper;

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
    public EventFullDto getEventByIdAndInitiator(long userId, long eventId) {
        Event event = getEventByIdOrException(userId, eventId);
        return mapper.convertEventToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, NewEventDto newEventDto) {
        Event event = getEventByIdOrException(userId, eventId);
        checkStateEvent(event);
        setEventParameters(event, newEventDto);
        if (Objects.nonNull(newEventDto.getStateAction())) {
            setStateEvent(newEventDto.getStateAction(), event);
        }
        return mapper.convertEventToEventFullDto(eventRepository.save(event));
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
                        "Cannot publish the event because it's not in the right state: PUBLISHED");
                event.setPublishedOn(publishedDate);
            }
            setStateEvent(stateAction, event);
        }

        return mapper.convertEventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Optional<Long[]> users, String[] states, Optional<Long[]> categories,
                                                Optional<String> rangeStart, Optional<String> rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime start = rangeStart.isPresent() ? convertToDateTime(rangeStart.get()) : LocalDateTime.MIN;
        LocalDateTime end = rangeEnd.isPresent() ? convertToDateTime(rangeEnd.get()) : LocalDateTime.MAX;
        return selectMethodByParameters(users, states, categories, start, end, pageRequest).stream()
                .map(mapper::convertEventToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid,
                                        Optional<String> rangeStart, Optional<String> rangeEnd, Boolean onlyAvailable,
                                        Optional<String> sort, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime start = rangeStart.isPresent() ? convertToDateTime(rangeStart.get()) : LocalDateTime.now();
        LocalDateTime end = rangeEnd.isPresent() ? convertToDateTime(rangeEnd.get()) : LocalDateTime.MAX;
        return null;
    }

    private void compareDate(LocalDateTime dateMastBefore, LocalDateTime dateMastAfter, String errorMassege) {
        if (dateMastBefore.isAfter(dateMastAfter)) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                    errorMassege, LocalDateTime.now());
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

    private ObjectNotSatisfyRulesException getEventNotSatisfyRulesException(String massege) {
        throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                massege,
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

    private Page<Event> selectMethodForGetEvents(Optional<String> text, Optional<Long[]> categories, Optional<Boolean> paid,
                                                 LocalDateTime start, LocalDateTime end,
                                                 Boolean onlyAvailable,
                                                 Optional<String> sort,
                                                 PageRequest pageRequest) {
        if (text.isPresent() && categories.isPresent() && paid.isPresent() && sort.isEmpty()) {
            return null;
            //eventRepository.getEventByTextCategoriesPaid(text.get(), Arrays.asList(categories.get()), paid.get(), start, onlyAvailable, end, pageRequest);
        }
        return null;
    }
}

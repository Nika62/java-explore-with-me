package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.model.exception.Request;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;
import static ru.practicum.ewm.model.enums.RequestsStatus.CONFIRMED;
import static ru.practicum.ewm.model.enums.RequestsStatus.PENDING;

@Service
@RequiredArgsConstructor
public class RequestServiceIml implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventRepository eventRepository;

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        LocalDateTime created = LocalDateTime.now();
        Event event = getEvent(eventId);
        checkRequestIsValid(event, userId);
        Request request;
        try {
            request = requestRepository.save(getRequestByCreate(event, created, userId));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectNotSatisfyRulesException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return requestMapper.convertRequestToRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequests(long userId) {
        return requestRepository.getRequestByRequestorId(userId).stream().map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList());
    }

    private void checkUserIsInitiator(Event event, long userId) {
        if (event.getInitiator().getId() == userId) {
            getNotSatisfyRulesException("The initiator cannot submit an application for participation in the event");

        }
    }

    private void checkStateEvenIsPublished(Event event) {
        if (!event.getState().equals(PUBLISHED.name())) {
            getNotSatisfyRulesException("It is impossible to participate in an unpublished event");
        }
    }

    private void checkEventLimitNotExceeded(Event event) {
        if (event.getParticipantLimit() - event.getConfirmedRequests() < 0) {
            getNotSatisfyRulesException("The event has reached the limit of requests for participation");
        }
    }

    private ObjectNotSatisfyRulesException getNotSatisfyRulesException(String errorMessage) {
        throw new ObjectNotSatisfyRulesException("Integrity constraint has been violated.",
                errorMessage, LocalDateTime.now());
    }

    private void setRequestStatus(Request request, Event event) {
        if (event.getRequestModeration().equals(false)) {
            request.setStatus(CONFIRMED.name());
            return;
        }
        request.setStatus(PENDING.name());
    }

    private Request getRequestByCreate(Event event, LocalDateTime dateTime, long userId) {
        Request request = new Request();
        setRequestStatus(request, event);
        request.setCreated(dateTime);
        request.setRequester(new User(userId));
        request.setEvent(event);

        return request;
    }

    private void checkRequestIsValid(Event event, long userId) {
        checkUserIsInitiator(event, userId);
        checkStateEvenIsPublished(event);
        checkStateEvenIsPublished(event);
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Integrity constraint has been violated.",
                        "Event with id=" + eventId + "was not found", LocalDateTime.now())
        );
    }
}

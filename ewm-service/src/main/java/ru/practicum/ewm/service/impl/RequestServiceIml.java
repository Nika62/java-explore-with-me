package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.enums.PublicationStatus.CANCELED;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;
import static ru.practicum.ewm.model.enums.RequestsStatus.CONFIRMED;
import static ru.practicum.ewm.model.enums.RequestsStatus.PENDING;
import static ru.practicum.ewm.model.enums.RequestsStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class RequestServiceIml implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

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
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + "was not found", LocalDateTime.now());
        }
        return requestRepository.getRequestsByRequesterId(userId).stream().map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.getRequestByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Request with id=" + requestId + " was not found", LocalDateTime.now())
        );
        request.setStatus(CANCELED.name());
        return requestMapper.convertRequestToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getUserEventRequests(long userId, long eventId) {
        return requestRepository.getRequestsByEventIdAndEventInitiatorId(eventId, userId).stream()
                .map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto reviewEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequestDto body) {
        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();
        Event event = getEvent(eventId);
        List<Long> requestsIds = body.getRequestIds();
        String status = body.getStatus();
        checkEventLimitNotExceeded(event);

        if (status.equals(CONFIRMED.name())) {
            if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
                result.setConfirmedRequests(requestRepository.getRequestsByIdIn(requestsIds).stream()
                        .map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList()));
                event.setConfirmedRequests(event.getConfirmedRequests() + requestsIds.size());
                eventRepository.save(event);
                return result;
            }
            return updateStatusEventRequests(requestsIds, event.getParticipantLimit() - event.getConfirmedRequests(), event);
        }
        result.setRejectedRequests(updateStatusEventRequests(requestsIds, status));
        return result;

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
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
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
        checkEventLimitNotExceeded(event);
    }

    private Event getEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Integrity constraint has been violated.",
                        "Event with id=" + eventId + "was not found", LocalDateTime.now())
        );
    }

    private EventRequestStatusUpdateResultDto updateStatusEventRequests(List<Long> allRequests, Long freeSeats, Event event) {
        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();

        if (allRequests.size() <= freeSeats) {
            result.setConfirmedRequests(updateStatusEventRequests(allRequests, CONFIRMED.name()));
            event.setConfirmedRequests(event.getConfirmedRequests() + allRequests.size());
            eventRepository.save(event);
            return result;
        }
        List<Long> confirmedRequestsIds = allRequests.stream().limit(freeSeats).collect(Collectors.toList());

        List<Long> rejectedRequestsIds = allRequests.stream().filter(id -> !confirmedRequestsIds.contains(id)).collect(Collectors.toList());

        result.setConfirmedRequests(updateStatusEventRequests(confirmedRequestsIds, CONFIRMED.name()));
        result.setRejectedRequests(updateStatusEventRequests(rejectedRequestsIds, REJECTED.name()));
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequestsIds.size());
        eventRepository.save(event);
        return result;
    }

    private List<RequestDto> updateStatusEventRequests(List<Long> requestsIds, String status) {
        requestRepository.updateAllRequestsStatus(requestsIds, status);
        return requestRepository.getRequestsByIdIn(requestsIds)
                .stream().map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList());
    }

}

package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.DeletionBlockedException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.model.enums.RequestsStatus.CONFIRMED;

@Component
@AllArgsConstructor
public class HelperCheckEntity {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;


    public void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + " was not found.", LocalDateTime.now());
        }
    }


    public void checkEventExists(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found.", LocalDateTime.now());
        }
    }

    public void checkUserAttendedEvent(long eventId, long userId) {
        if (!requestRepository.existsByEventIdAndRequesterIdAndStatus(eventId, userId, CONFIRMED.name())) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.", "The user is not attended the event",
                    LocalDateTime.now());
        }
    }


    public User getUserOrException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + " was not found.", LocalDateTime.now()));
    }


    public Event getEventOrException(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found.", LocalDateTime.now()));
    }


    public void checkCategoryEmpty(long catId) {
        if (eventRepository.existsEventByCategoryId(catId)) {
            throw new DeletionBlockedException("For the requested operation the conditions are not met.", "The category is not empty", LocalDateTime.now());
        }
    }


    public List<Event> getEventsByIdIn(List<Long> ids) {
        return eventRepository.getEventsByIdIn(ids);
    }
}

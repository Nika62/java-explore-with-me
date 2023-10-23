package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;

import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;


    @Override
    public CommentDto addComment(long userId, long eventId, String text) {
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime test = LocalDateTime.of(9099, 12, 12, 12, 12, 12);
        User author = getUserOrException(userId);
        Event event = getEventOrException(eventId);
        checkUserNotInitiator(event, userId);
//        event.setState(PUBLISHED.name());
        checkEventTookPlace(event, test);
        Comment comment;
        try {
            comment = commentRepository.save(new Comment(author, text, event, createdOn));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return commentMapper.convertCommentToCommentDto(comment);
    }

    private void checkUserNotInitiator(Event event, long userId) {
        if (event.getInitiator().getId() == userId) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                    "The initiator of the event cannot leave a comment on it", LocalDateTime.now());
        }
    }

    private void checkEventTookPlace(Event event, LocalDateTime dateTime) {
        if (event.getState().equals(PUBLISHED.name())) {
            if (event.getEventDate().isAfter(dateTime)) {
                throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                        "You cannot leave a comment on an event that has not yet taken place.", LocalDateTime.now());
            }
        } else {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.",
                    "You cannot leave a comment on an event that is not published.", LocalDateTime.now());
        }
    }

    private User getUserOrException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + " was not found.", LocalDateTime.now()));
    }

    private Event getEventOrException(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found.", LocalDateTime.now()));
    }
}

package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.DeletionBlockedException;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;
import static ru.practicum.ewm.model.enums.RequestsStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final CommentMapper commentMapper;


    @Override
    public CommentDto addComment(long userId, long eventId, String text) {
        LocalDateTime createdOn = LocalDateTime.now();
        User author = getUserOrException(userId);
        Event event = getEventOrException(eventId);
        checkUserNotInitiator(event, userId);
        checkEventTookPlace(event, createdOn);
        checkUserAttendedEvent(eventId, userId);
        Comment comment;
        try {
            comment = commentRepository.save(new Comment(author, text, event, createdOn));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return commentMapper.convertCommentToCommentDto(comment);
    }

    @Override
    public List<CommentUserDto> getCommentsUser(long userId, int from, int size) {
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return commentRepository.getCommentsByUserId(userId, pageRequest).stream().map(commentMapper::convertCommentToCommentUserDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsUserToEvent(long userId, long eventId) {
        checkUserExists(userId);
        checkEventExists(eventId);
        return commentRepository.getCommentsByUserIdAndEventId(userId, eventId).stream().map(commentMapper::convertCommentToCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCommentById(long userId, long eventId, long commentId) {
        checkUserExists(userId);
        checkEventExists(eventId);
        Comment comment = getCommentOrException(commentId);
        if (comment.getUser().getId() != userId) {
            throw new DeletionBlockedException("For the requested operation the conditions are not met.",
                    "The user is not the author of the comment", LocalDateTime.now());
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentUserDto updateComment(long userId, long eventId, long commentId, String text) {
        checkUserExists(userId);
        checkEventExists(eventId);
        Comment comment = getCommentOrException(commentId);
        checkUserAuthorComment(comment, userId);
        comment.setText(text);
        try {
            return commentMapper.convertCommentToCommentUserDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
    }

    @Override
    public void deleteCommentById(long commentId) {

    }

    private void checkUserAttendedEvent(long eventId, long userId) {
        if (!requestRepository.existsByEventIdAndUserIdAndStatus(eventId, userId, CONFIRMED.name())) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.", "The user is not attended the event",
                    LocalDateTime.now());
        }
    }

    private void checkUserAuthorComment(Comment comment, long userId) {
        if (comment.getUser().getId() != userId) {
            throw new ObjectNotSatisfyRulesException("For the requested operation the conditions are not met.", "The user is not the author of the comment",
                    LocalDateTime.now());
        }

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

    private Comment getCommentOrException(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Comment with id=" + commentId + " was not found.", LocalDateTime.now()));
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + " was not found.", LocalDateTime.now());
        }
    }

    private void checkEventExists(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException("The required object was not found.", "Event with id=" + eventId + " was not found.", LocalDateTime.now());
        }
    }

    private void checkCommentExists(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ObjectNotFoundException("The required object was not found.", "Comment with id=" + commentId + " was not found.", LocalDateTime.now());
        }
    }
}

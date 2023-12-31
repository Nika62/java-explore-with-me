package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.DeletionBlockedException;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final EventMapper eventMapper;

    private final HelperCheckEntity helperCheckEntity;


    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        LocalDateTime createdOn = convertToDateTime(newCommentDto.getCreatedOn());
        User author = helperCheckEntity.getUserOrException(userId);
        Event event = helperCheckEntity.getEventOrException(eventId);
        checkUserNotInitiator(event, userId);
        checkEventTookPlace(event, createdOn);
        helperCheckEntity.checkUserAttendedEvent(eventId, userId);
        Comment comment;
        try {
            comment = commentRepository.save(new Comment(author, newCommentDto.getText(), event, createdOn));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return commentMapper.convertCommentToCommentDto(comment);
    }

    @Override
    public List<CommentUserDto> getCommentsUser(long userId, int from, int size) {
        helperCheckEntity.checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.getCommentsByUserIdOrderById(userId, pageRequest).toList();
        List<CommentUserDto> commentsDto = comments.stream().map(commentMapper::convertCommentToCommentUserDto).collect(Collectors.toList());
        addEventInListCommentUserDto(comments, commentsDto);
        return commentsDto;
    }

    @Override
    public List<CommentDto> getCommentsUserToEvent(long userId, long eventId) {
        helperCheckEntity.checkUserExists(userId);
        helperCheckEntity.checkEventExists(eventId);
        return commentRepository.getCommentsByUserIdAndEventIdOrderById(userId, eventId).stream().map(commentMapper::convertCommentToCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByUser(long userId, long eventId, long commentId) {
        helperCheckEntity.checkUserExists(userId);
        helperCheckEntity.checkEventExists(eventId);
        Comment comment = getCommentOrException(commentId);
        if (comment.getUser().getId() != userId) {
            throw new DeletionBlockedException("For the requested operation the conditions are not met.",
                    "The user is not the author of the comment", LocalDateTime.now());
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentUserDto updateComment(long userId, long eventId, long commentId, String text) {
        helperCheckEntity.checkUserExists(userId);
        helperCheckEntity.checkEventExists(eventId);
        Comment comment = getCommentOrException(commentId);
        checkUserAuthorComment(comment, userId);
        comment.setText(text);
        CommentUserDto commentUserDto = commentMapper.convertCommentToCommentUserDto(commentRepository.save(comment));
        commentUserDto.setEvent(eventMapper.convertEventToEventForCommentDto(comment.getEvent()));
        return commentUserDto;
    }


    @Override
    public List<CommentDto> getCommentsEvent(long eventId, int from, int size) {
        helperCheckEntity.checkEventExists(eventId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return commentRepository.getCommentsByEventIdOrderById(eventId, pageRequest).stream()
                .map(commentMapper::convertCommentToCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentFullDto getCommentByIdAndByEventId(long eventId, long commentId) {
        helperCheckEntity.checkEventExists(eventId);
        Comment comment = getCommentOrException(commentId);
        return getCommentFullDtoWithEvent(comment);
    }

    @Override
    public void deleteCommentByAdmin(long commentId) {
        checkCommentExists(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentFullDto getCommentById(long commentId) {
        Comment comment = getCommentOrException(commentId);
        return getCommentFullDtoWithEvent(comment);
    }

    @Override
    public List<CommentFullDto> getCommentsByAdmin(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByOrderByIdAsc(pageRequest).toList();
        List<CommentFullDto> commentsDto = comments.stream().map(commentMapper::convertCommentToCommentFullDto).collect(Collectors.toList());
        addEventInListCommentUserDto(comments, commentsDto);
        return commentsDto;
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

    private Comment getCommentOrException(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Comment with id=" + commentId + " was not found.", LocalDateTime.now()));
    }

    private void checkCommentExists(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ObjectNotFoundException("The required object was not found.", "Comment with id=" + commentId + " was not found.", LocalDateTime.now());
        }
    }

    private void addEventInListCommentUserDto(List<Comment> comments, List<? extends CommentUserDto> commentsDto) {
        IntStream.range(0, commentsDto.size())
                .forEach(i -> commentsDto.get(i).setEvent(eventMapper.convertEventToEventForCommentDto(comments.get(i).getEvent())));
    }

    private CommentFullDto getCommentFullDtoWithEvent(Comment comment) {
        CommentFullDto commentDto = commentMapper.convertCommentToCommentFullDto(comment);
        commentDto.setEvent(eventMapper.convertEventToEventForCommentDto(comment.getEvent()));
        return commentDto;
    }
}

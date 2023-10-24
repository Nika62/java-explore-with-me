package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(long userId, long eventId, String text);

    List<CommentUserDto> getCommentsUser(long userId, int from, int size);

    List<CommentDto> getCommentsUserToEvent(long userId, long eventId);

    void deleteCommentById(long userId, long eventId, long commentId);

    CommentUserDto updateComment(long userId, long eventId, long commentId, String text);

    void deleteCommentById(long commentId);
}

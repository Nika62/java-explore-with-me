package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto);

    List<CommentUserDto> getCommentsUser(long userId, int from, int size);

    List<CommentDto> getCommentsUserToEvent(long userId, long eventId);

    void deleteCommentByUser(long userId, long eventId, long commentId);

    CommentUserDto updateComment(long userId, long eventId, long commentId, String text);

    List<CommentDto> getCommentsEvent(long eventId, int from, int size);

    CommentFullDto getCommentByIdAndByEventId(long eventId, long commentId);

    void deleteCommentByAdmin(long commentId);

    CommentFullDto getCommentById(long commentId);

    List<CommentFullDto> getCommentsByAdmin(int from, int size);
}

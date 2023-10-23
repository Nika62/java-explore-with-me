package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;

public interface CommentService {

    CommentDto addComment(long userId, long eventId, String text);
}

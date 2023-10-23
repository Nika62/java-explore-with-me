package ru.practicum.ewm.dto.comment;

import ru.practicum.ewm.dto.user.UserDto;

public class CommentShortDto {
    private long id;

    private UserDto user;

    private String text;

    private String createdOn;
}

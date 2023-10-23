package ru.practicum.ewm.dto.comment;

import lombok.Data;

@Data
public class CommentDto {

    private long id;

    private String userName;

    private String text;

    private String createdOn;
}

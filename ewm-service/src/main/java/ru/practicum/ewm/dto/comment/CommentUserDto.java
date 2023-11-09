package ru.practicum.ewm.dto.comment;

import lombok.Data;
import ru.practicum.ewm.dto.event.EventForCommentDto;

@Data
public class CommentUserDto {
    private long id;
    private EventForCommentDto event;
    private String text;
    private String createdOn;
}

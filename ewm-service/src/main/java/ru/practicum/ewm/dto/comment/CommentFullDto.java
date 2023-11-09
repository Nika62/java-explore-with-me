package ru.practicum.ewm.dto.comment;

import lombok.Data;
import ru.practicum.ewm.dto.event.EventForCommentDto;

@Data
public class CommentFullDto extends CommentUserDto {
    private long id;
    private EventForCommentDto event;
    private String userName;
    private String text;
    private String createdOn;
}

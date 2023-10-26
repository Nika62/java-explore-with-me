package ru.practicum.ewm.dto.event;

import lombok.Data;

@Data
public class EventForCommentDto {
    private long id;
    private String annotation;
    private String eventDate;
    private String title;
}

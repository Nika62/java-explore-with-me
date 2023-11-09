package ru.practicum.ewm.dto.event;

import lombok.Data;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.enums.PublicationStatus;

import java.util.List;

@Data
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private PublicationStatus state;
    private String title;
    private long views;
    private List<CommentDto> comments;

}

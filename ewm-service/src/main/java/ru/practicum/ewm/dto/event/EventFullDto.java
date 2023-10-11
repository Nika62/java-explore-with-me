package ru.practicum.ewm.dto.event;

import lombok.Data;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Location;

@Data
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    //дата и время создания события
    private String createdOn;
    private String description;
    //время на которое намечено событие
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    // ограничение на кол-во участников
    private int participantLimit;
    //дата публикации события
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    //кол-во просмотров
    private long views;

}

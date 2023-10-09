package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;

public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    //время на которое намечено событие
    private String eventDate;
    private long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    // кол-во просмотров
    private long views;
}

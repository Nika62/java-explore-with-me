package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class CreateEventDto {
    @NotBlank
    @Max(255)
    private String annotation;
    @NotNull
    @Positive
    private CategoryDto category;
    //дата и время создания события
    @Pattern(regexp = "^(0[1-9]|1\\d|2[0-8]|29(?=-\\d\\d-(?!1[01345789]00|2[1235679]00)\\d\\d(?:[02468][048]|[13579][26]))" +
            "|30(?!-02)|31(?=-0[13578]|-1[02]))-(0[1-9]|1[0-2])-([12]\\d{3}) ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$")
    private String createdOn;
    private String description;
    //время на которое намечено событие
    private String eventDate;
    private long initiator;
    private Location location;
    private Boolean paid;
    // ограничение на кол-во участников
    private int participantLimit;
    private Boolean requestModeration;
    private String state;
    private String title;

}

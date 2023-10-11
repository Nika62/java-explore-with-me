package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(max = 2000, min = 20)
    private String annotation;
    @NotNull
    @Positive
    private long category;
    //дата и время создания события
    @NotBlank
    @Size(max = 7000, min = 20)
    private String description;
    //время на которое намечено событие
    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1\\d|2[0-8]|29(?=-\\d\\d-(?!1[01345789]00|2[1235679]00)\\d\\d(?:[02468][048]|[13579][26]))" +
            "|30(?!-02)|31(?=-0[13578]|-1[02]))-(0[1-9]|1[0-2])-([12]\\d{3}) ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$")
    private String eventDate;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    // ограничение на кол-во участников
    @Positive
    private int participantLimit = 0;
    private Boolean requestModeration = true;
    private String state;
    @NotNull
    @Size(max = 120, min = 3)
    private String title;

}

package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.enums.StateAction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(max = 2000, min = 20)
    private String annotation;
    @NotNull
    @Positive
    private Long category;
    @NotBlank
    @Size(max = 7000, min = 20)
    private String description;
    //время на которое намечено событие
    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")
    private String eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    // ограничение на кол-во участников
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotNull
    @Size(max = 120, min = 3)
    private String title;

    private StateAction stateAction;

}

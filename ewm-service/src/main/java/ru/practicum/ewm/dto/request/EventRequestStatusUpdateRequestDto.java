package ru.practicum.ewm.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    List<Long> requestIds;
    @NotBlank
    String status;
}

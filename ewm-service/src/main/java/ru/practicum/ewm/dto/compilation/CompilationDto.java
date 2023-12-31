package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompilationDto {

    private long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events = new ArrayList<>();
}

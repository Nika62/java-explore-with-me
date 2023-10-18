package ru.practicum.ewm.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewCompilationDto {
    private long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;

    private List<Long> events = new ArrayList<>();
}

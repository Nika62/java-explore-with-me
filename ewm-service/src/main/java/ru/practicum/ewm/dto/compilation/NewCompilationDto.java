package ru.practicum.ewm.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewCompilationDto {
    private long id;
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events = new ArrayList<>();
}

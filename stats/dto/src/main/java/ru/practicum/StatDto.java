package ru.practicum;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
public class StatDto {
    @NotBlank
    private String app;
    @NotBlank
    private String ip;

    private long hits;
}

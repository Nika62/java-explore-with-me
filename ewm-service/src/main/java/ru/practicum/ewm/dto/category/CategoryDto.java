package ru.practicum.ewm.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class CategoryDto {
    private long id;
    @NotBlank
    @Size(max = 50, min = 1)
    private String name;
}

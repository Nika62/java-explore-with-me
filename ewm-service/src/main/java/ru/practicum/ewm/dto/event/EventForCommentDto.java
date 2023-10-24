package ru.practicum.ewm.dto.event;

import lombok.Data;
import ru.practicum.ewm.dto.category.CategoryDto;

@Data
public class EventForCommentDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private String eventDate;
    private String title;
}

package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterEvent {
    private Optional<String> text;
    private Optional<Long[]> categories;
    private Optional<Boolean> paid;
    private Optional<String> rangeStart;
    private Optional<String> rangeEnd;
    private Boolean onlyAvailable;
}

package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterEventAdm {
    private Optional<Long[]> users;
    private Optional<String[]> states;
    private Optional<Long[]> categories;
    private Optional<String> rangeStart;
    private Optional<String> rangeEnd;
}

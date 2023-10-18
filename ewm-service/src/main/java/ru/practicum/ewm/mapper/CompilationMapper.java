package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.Compilation;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDto convertCompilationToCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        if (Objects.nonNull(compilation.getEvents()) && compilation.getEvents().size() > 0) {
            compilationDto.setEvents(
                    compilation.getEvents().stream().map(eventMapper::convertEventToEventShortDto).collect(Collectors.toList()));
        } else {
            compilationDto.setEvents(new ArrayList<EventShortDto>());
        }
        return compilationDto;
    }

    public Compilation convertNewCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        return compilation;
    }
}

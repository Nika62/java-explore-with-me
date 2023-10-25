package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(long comId);

    CompilationDto updateCompilation(long compId, NewCompilationDto newCompilationDto);

    List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size);

    CompilationDto getCompilationById(long compId);


}

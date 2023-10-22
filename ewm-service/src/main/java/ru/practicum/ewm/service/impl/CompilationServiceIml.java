package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ValidationException;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceIml implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.convertNewCompilationDtoToCompilation(newCompilationDto);
        setEventsInCompilation(compilation, newCompilationDto);
        return compilationMapper.convertCompilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(long compId) {
        checkCompilationIsExists(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(long compId, NewCompilationDto newCompilationDto) {
        checkCompilationIsExists(compId);
        Compilation compilation = compilationRepository.getById(compId);
        checkCompilationTitle(newCompilationDto);
        compilation.setPinned(Objects.nonNull(newCompilationDto.getPinned()) ? newCompilationDto.getPinned() : compilation.getPinned());
        compilation.setTitle(Objects.nonNull(newCompilationDto.getTitle()) ? newCompilationDto.getTitle() : compilation.getTitle());
        setEventsInCompilation(compilation, newCompilationDto);
        return compilationMapper.convertCompilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (pinned.isPresent()) {
            return compilationRepository.getCompilationsByPinned(pinned.get(), pageRequest).stream()
                    .map(compilationMapper::convertCompilationToCompilationDto).collect(Collectors.toList());
        }
        return compilationRepository.findAll(pageRequest).stream()
                .map(compilationMapper::convertCompilationToCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.",
                        "Compilation with id=" + compId + " was not found", LocalDateTime.now())
        );
        return compilationMapper.convertCompilationToCompilationDto(compilation);
    }

    private void checkCompilationIsExists(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ObjectNotFoundException("The required object was not found.",
                    "Compilation with id=" + compId + " was not found", LocalDateTime.now());
        }
    }

    private void setEventsInCompilation(Compilation compilation, NewCompilationDto newCompilationDto) {
        if (Objects.nonNull(newCompilationDto.getEvents()) && !newCompilationDto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.getEventsByIdIn(newCompilationDto.getEvents()));
            compilation.setEvents(events);
        }
    }

    private void checkCompilationTitle(NewCompilationDto newCompilationDto) {
        if (Objects.nonNull(newCompilationDto.getTitle()) && newCompilationDto.getTitle().length() < 50 && newCompilationDto.getTitle().length() > 1) {
            throw new ValidationException("Incorrectly made request.", "Annotation length should be from 1 to 50", LocalDateTime.now());
        }
    }
}

package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;
import ru.practicum.ewm.mapper.PlaceMapper;
import ru.practicum.ewm.model.Place;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.repository.PlaceRepository;
import ru.practicum.ewm.service.PlaceService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @Override
    public PlaceFullDto addPlace(NewPlaceDto newPlaceDto) {
        Place place;
        try {
            place = placeRepository.save(placeMapper.convertToPlace(newPlaceDto));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return placeMapper.convertToPlaceFullDto(place);
    }

    @Override
    public List<PlaceFullDto> getPlaces(int from, int size) {
        return placeRepository.findAll(PageRequest.of(from / size, size))
                .map(placeMapper::convertToPlaceFullDto).toList();
    }

    @Override
    public PlaceFullDto getPlaceById(long id) {
        Place place = placeRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("The required object was not found.", "Place with id=" + id + " was not found", LocalDateTime.now()));
        return placeMapper.convertToPlaceFullDto(place);
    }
}

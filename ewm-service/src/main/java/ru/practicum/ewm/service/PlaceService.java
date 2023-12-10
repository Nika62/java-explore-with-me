package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;

import java.util.List;

public interface PlaceService {

    PlaceFullDto addPlace(NewPlaceDto newPlaceDto);

    List<PlaceFullDto> getPlaces(int from, int size);

    PlaceFullDto getPlaceById(long id);
}

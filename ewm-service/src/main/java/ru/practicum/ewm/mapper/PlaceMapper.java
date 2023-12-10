package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;
import ru.practicum.ewm.model.Place;

@Component
public class PlaceMapper {

    public Place convertToPlace(NewPlaceDto newPlaceDto) {
        if (newPlaceDto == null) {
            return null;
        }
        return new Place(newPlaceDto.getName(), newPlaceDto.getLat(), newPlaceDto.getLon(), newPlaceDto.getRadius());
    }

    public PlaceFullDto convertToPlaceFullDto(Place place) {
        if (place == null) {
            return null;
        }

        return new PlaceFullDto(place.getId(), place.getName(), place.getLatitude(), place.getLongitude(), place.getRadius());
    }

}

package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.service.PlaceService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.ewm.HelperLocationTestEntity.getNewLocationDto;
import static ru.practicum.ewm.HelperLocationTestEntity.getNewLocationDto2;
import static ru.practicum.ewm.HelperLocationTestEntity.getNewLocationDto3;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlaceServiceImplTest {

    @Autowired
    private final PlaceService placeService;
    private NewPlaceDto newPlaceDto;

    @BeforeEach
    public void before() {
        newPlaceDto = getNewLocationDto();
    }

    @Test
    void shouldAddNewLocation() {

        PlaceFullDto loc = placeService.addLocation(newPlaceDto);
        assertEquals(loc.getId(), 1);
        assertEquals(loc.getName(), newPlaceDto.getName());
        assertEquals(loc.getLat(), newPlaceDto.getLat());
        assertEquals(loc.getLon(), newPlaceDto.getLon());
        assertEquals(loc.getRadius(), newPlaceDto.getRadius());
    }

    @Test
    void shouldReturnExceptionWhenAddRepeatedLocation() {
        PlaceFullDto loc = placeService.addLocation(newPlaceDto);
        Exception exception = assertThrows(ObjectAlreadyExistsException.class,
                () -> {
                    placeService.addLocation(newPlaceDto);
                });

        assertEquals("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement",
                exception.getMessage());

    }

    @Test
    void shouldReturnListLocations() {
        placeService.addLocation(newPlaceDto);
        List<PlaceFullDto> locations = placeService.getLocations(0, 10);
        assertTrue(Objects.nonNull(locations));
        assertEquals(locations.size(), 1);
        assertEquals(locations.get(0).getName(), newPlaceDto.getName());
        assertEquals(locations.get(0).getLat(), newPlaceDto.getLat());
        assertEquals(locations.get(0).getLon(), newPlaceDto.getLon());
        assertEquals(locations.get(0).getRadius(), newPlaceDto.getRadius());
    }

    @Test
    void shouldReturnListLocationsSize2() {
        placeService.addLocation(newPlaceDto);
        placeService.addLocation(getNewLocationDto2());
        placeService.addLocation(getNewLocationDto3());
        List<PlaceFullDto> locations = placeService.getLocations(0, 2);
        assertTrue(Objects.nonNull(locations));
        assertEquals(locations.size(), 2);
        assertEquals(locations.get(0).getName(), newPlaceDto.getName());
        assertEquals(locations.get(1).getName(), getNewLocationDto2().getName());

    }

    @Test
    void shouldReturnEmptyListLocations() {
        List<PlaceFullDto> locations = placeService.getLocations(0, 10);
        assertTrue(Objects.nonNull(locations));
        assertEquals(locations.size(), 0);
    }

    @Test
    void shouldReturnLocationById() {
        placeService.addLocation(newPlaceDto);
        PlaceFullDto loc = placeService.getLocationById(1);
        assertTrue(Objects.nonNull(loc));
        assertEquals(loc.getId(), 1);
        assertEquals(loc.getName(), newPlaceDto.getName());
        assertEquals(loc.getLat(), newPlaceDto.getLat());
        assertEquals(loc.getLon(), newPlaceDto.getLon());
        assertEquals(loc.getRadius(), newPlaceDto.getRadius());

    }

    @Test
    void shouldReturnExceptionGetLocationWrongId() {
        Exception exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> {
                    placeService.getLocationById(1999);
                });

        assertEquals("Location with id=1999 was not found", exception.getMessage());
    }

}
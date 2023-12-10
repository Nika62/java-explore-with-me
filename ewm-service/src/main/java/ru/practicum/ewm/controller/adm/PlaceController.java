package ru.practicum.ewm.controller.adm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;
import ru.practicum.ewm.service.PlaceService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/locations")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    @ResponseStatus(CREATED)
    public PlaceFullDto addPlace(@RequestBody @Valid NewPlaceDto newPlaceDto) {
        return placeService.addPlace(newPlaceDto);
    }

    @GetMapping
    List<PlaceFullDto> getPlaces(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return placeService.getPlaces(from, size);
    }

    @GetMapping("/{id}")
    PlaceFullDto getPlaceById(@PathVariable long id) {
        return placeService.getPlaceById(id);
    }

}

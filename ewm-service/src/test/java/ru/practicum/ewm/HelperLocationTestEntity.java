package ru.practicum.ewm;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;

@Component
public class HelperLocationTestEntity {

    public static NewPlaceDto getNewLocationDto() {
        return new NewPlaceDto("Pязань", 12.33F, 17.18F, 90);
    }

    public static NewPlaceDto getNewLocationDto2() {
        return new NewPlaceDto("Kaзань", 88.33F, 99.18F, 1000);
    }

    public static NewPlaceDto getNewLocationDto3() {
        return new NewPlaceDto("Kняжое", 30.33F, 20.18F, 190);
    }

    public static PlaceFullDto getLocationFullDto() {
        return new PlaceFullDto(1L, "Pязань", 12.33F, 17.18F, 90);
    }

    public static NewPlaceDto getLocationDtoWithoutName() {
        return new NewPlaceDto(12.33F, 17.18F, 90);
    }

}

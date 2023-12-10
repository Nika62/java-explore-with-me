package ru.practicum.ewm.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceFullDto {

    private long id;

    private String name;

    private float lat;

    private float lon;

    private int radius;

}

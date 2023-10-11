package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Location {
    // широта
    private float lat;

    //долготa
    private float lon;

    public Location(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}

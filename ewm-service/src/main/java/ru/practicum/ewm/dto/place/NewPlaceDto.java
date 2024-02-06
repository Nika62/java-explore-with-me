package ru.practicum.ewm.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPlaceDto {
    @NotBlank
    private String name;
    @NotNull
    private float lat;
    @NotNull
    private float lon;
    @NotNull
    @PositiveOrZero
    private int radius;

    public NewPlaceDto(float lat, float lon, int radius) {
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
    }
}

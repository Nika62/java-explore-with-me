package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private float latitude;
    @Column
    private float longitude;
    @Column
    private int radius;

    public Place(String name, float lat, float lon, int radius) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.radius = radius;
    }
}

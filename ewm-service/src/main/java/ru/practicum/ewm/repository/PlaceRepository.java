package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}

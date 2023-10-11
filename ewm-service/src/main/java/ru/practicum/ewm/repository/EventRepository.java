package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsEventByCategoryId(long id);
}

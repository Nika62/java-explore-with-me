package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsEventByCategoryId(long id);

    Page<Event> getAllEventByInitiatorId(long userId, PageRequest pageRequest);

    Optional<Event> getEventByIdAndInitiatorId(long eventId, long userId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id in ?1 AND e.state IN ?2 AND e.category.id IN ?3 AND e.eventDate BETWEEN ?4 AND ?5")
    Page<Event> getEventByUsesStatesCategoriesAndEventDate(List<Long> users, List<String> states, List<Long> categories,
                                                           LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.initiator.id in ?1 AND e.state IN ?2 AND e.eventDate BETWEEN ?3 AND ?4")
    Page<Event> getEventByUsesStatesAndEventDate(List<Long> users, List<String> states,
                                                 LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.state IN ?1 AND e.category.id IN ?2 AND e.eventDate BETWEEN ?3 AND ?4")
    Page<Event> getEventByStatesCategoriesAndEventDate(List<String> states, List<Long> categories,
                                                       LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE  lower(e.annotation) like ?1 or lower(e.description) like ?1 AND e.category.id  IN ?2 AND e.paid =?3 AND  e.eventDate BETWEEN ?4 AND ?5")
    Page<Event> getEventByTextCategoriesPaid(String text, List<Long> categories, boolean paid, LocalDateTime start, LocalDateTime end,
                                             boolean onlyAvailable, PageRequest pageRequest);
}

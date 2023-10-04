package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT h.app, h.uri, count(distinct h.ip) FROM hits as h where h.creation_time between ?1 and ?2 and uri in ?3 GROUP BY h.app, h.uri;", nativeQuery = true)
    List<StatDto> getStatsByAllParameter(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits as h where h.creation_time between ?1 and ?2 and uri in ?3 GROUP BY h.app, h.uri;", nativeQuery = true)
    List<StatDto> getStatsByDateTimeAndUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "SELECT h.app, h.uri, count(distinct h.ip) FROM hits as h where h.creation_time between ?1 and ?2 GROUP BY h.app, h.uri;", nativeQuery = true)
    List<StatDto> getStatsByDateTimeAndDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits as h where h.creation_time between ?1 and ?2 GROUP BY h.app, h.uri;", nativeQuery = true)
    List<StatDto> getStatsByDateTime(LocalDateTime start, LocalDateTime end);
}

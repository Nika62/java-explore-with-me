package ru.practicum.stats.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.stats.dto.StatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "FROM EndpointHit e where e.creationTime between ?1 and ?2 and e.uri in ?3 " +
            "GROUP BY e.app, e.uri ORDER BY count(e.ip) DESC")
    List<StatsDto> getStatsByAllParameterAndUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.StatsDto(e.app, e.uri, count(e.ip)) " +
            "FROM EndpointHit e where e.creationTime between ?1 and ?2 and e.uri in ?3 " +
            "GROUP BY e.app, e.uri ORDER BY count(e.ip) DESC")
    List<StatsDto> getStatsByAllParameterNoUnique(LocalDateTime start, LocalDateTime end,  List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.StatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "FROM EndpointHit e where e.creationTime between ?1 and ?2 " +
            "GROUP BY e.app, e.uri ORDER BY count(e.ip) DESC")
    List<StatsDto> getStatsByDateTimeAndUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.StatsDto(e.app, e.uri, count(e.ip)) " +
            "FROM EndpointHit e where e.creationTime between ?1 and ?2 " +
            "GROUP BY e.app, e.uri ORDER BY count(e.ip) DESC")
    List<StatsDto> getStatsByDateTimeNoUnique(LocalDateTime start, LocalDateTime end);
}

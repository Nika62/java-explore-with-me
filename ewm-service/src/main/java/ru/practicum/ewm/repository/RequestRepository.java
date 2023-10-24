package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Request;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getRequestsByRequesterId(long userId);

    Optional<Request> getRequestByIdAndRequesterId(long requestId, long userId);

    List<Request> getRequestsByEventIdAndEventInitiatorId(long eventId, long userId);

    List<Request> getRequestsByIdIn(List<Long> requestsIds);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status =?2 WHERE r.id IN ?1 ")
    void updateAllRequestsStatus(List<Long> requestsIds, String status);

    boolean existsByEventIdAndUserIdAndStatus(long eventId, long userId, String status);

}

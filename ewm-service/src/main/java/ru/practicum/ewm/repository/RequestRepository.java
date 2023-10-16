package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getRequestByRequester(long userId);

    Optional<Request> getRequestByIdAndRequester(long requestId, long userId);

}

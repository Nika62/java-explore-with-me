package ru.practicum.ewm.service.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.event.SearchFilterEvent;
import ru.practicum.ewm.dto.event.SearchFilterEventAdm;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;

@Component
public class EventSpecification {

    protected List<Specification<Event>> searchFilterToSpecifications(SearchFilterEvent filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getText().isEmpty() ? null : likeText(filter.getText()));
        specifications.add(filter.getCategories().isEmpty() ? null : categoryIn(filter.getCategories()));
        specifications.add(filter.getPaid().isEmpty() ? null : paidEqualsTo(filter.getPaid()));
        specifications.add(filter.getOnlyAvailable().equals(false) ? null : isOnlyAvailable(filter.getOnlyAvailable()));
        specifications.add(filter.getRangeStart().isEmpty() ? greaterThanTimeNow() : greaterThanOrEqualToRangeStart(filter.getRangeStart()));
        specifications.add(filter.getRangeEnd().isEmpty() ? null : lessThanOrEqualToRangeEnd(filter.getRangeEnd()));
        specifications.add(filter.getLat().isEmpty() ? null : filter.getLon().isEmpty() ? null : filter.getRadius().isEmpty() ? null
                ?)
        specifications.add(equalsPublished());
        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected List<Specification<Event>> searchFilterSpecificationsAdm(SearchFilterEventAdm filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getUsers().isEmpty() ? null : inUsers(filter.getUsers()));
        specifications.add(filter.getStates().isEmpty() ? null : inStates(filter.getStates()));
        specifications.add(filter.getCategories().isEmpty() ? null : categoryIn(filter.getCategories()));
        specifications.add(filter.getRangeStart().isEmpty() ? greaterThanTimeNow() : greaterThanOrEqualToRangeStart(filter.getRangeStart()));
        specifications.add(filter.getRangeEnd().isEmpty() ? null : lessThanOrEqualToRangeEnd(filter.getRangeEnd()));
        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Specification<Event> paidEqualsTo(Optional<Boolean> paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid.get());
    }

    private Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.equal(root.get("participantLimit"), 0),
                criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")));
    }

    private Specification<Event> categoryIn(Optional<Long[]> categoriesIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id")).value(Arrays.asList(categoriesIds.get()));
    }

    private Specification<Event> likeText(Optional<String> text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("annotation")), text.get());
    }

    private Specification<Event> greaterThanOrEqualToRangeStart(Optional<String> rangeStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), convertToDateTime(rangeStart.get()));
    }

    private Specification<Event> lessThanOrEqualToRangeEnd(Optional<String> rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), convertToDateTime(rangeEnd.get()));
    }

    private Specification<Event> greaterThanTimeNow() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now());
    }

    private Specification<Event> equalsPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), PUBLISHED.name());
    }

    private Specification<Event> inUsers(Optional<Long[]> users) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id")).value(Arrays.asList(users.get()));
    }

    private Specification<Event> inStates(Optional<String[]> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(Arrays.asList(states.get()));
    }

    private Specification<Event> inPlace(Optional<Float> lat, Optional<Float> lon, Optional<Integer> radius) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.


    }
}

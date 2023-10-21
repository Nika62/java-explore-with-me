package ru.practicum.ewm.service.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.event.SearchFilterEvent;
import ru.practicum.ewm.dto.event.SearchFilterEventAdm;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToDateTime;
import static ru.practicum.ewm.model.enums.PublicationStatus.PUBLISHED;

@Component
public class EventSpecification {

    protected List<Specification<Event>> searchFilterToSpecifications(SearchFilterEvent filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getText() == null ? null : likeText(filter.getText()));
        specifications.add(filter.getCategories() == null ? null : categoryIn(filter.getCategories()));
        specifications.add(filter.getPaid() == null ? null : paidEqualsTo(filter.getPaid()));
        specifications.add(filter.getOnlyAvailable().equals(false) ? null : isOnlyAvailable(filter.getOnlyAvailable()));
        specifications.add(filter.getRangeStart() == null ? greaterThanTimeNow() : greaterThanOrEqualToRangeStart(filter.getRangeStart()));
        specifications.add(filter.getRangeEnd() == null ? null : lessThanOrEqualToRangeEnd(filter.getRangeEnd()));
        specifications.add(equalsPublished());
        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected List<Specification<Event>> searchFilterSpecificationsAdm(SearchFilterEventAdm filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getUsers() == null ? null : inUsers(filter.getUsers()));
        specifications.add(filter.getStates() == null ? null : inStates(filter.getStates()));
        specifications.add(filter.getCategories() == null ? null : categoryIn(filter.getCategories()));
        specifications.add(filter.getRangeStart() == null ? greaterThanTimeNow() : greaterThanOrEqualToRangeStart(filter.getRangeStart()));
        specifications.add(filter.getRangeEnd() == null ? null : lessThanOrEqualToRangeEnd(filter.getRangeEnd()));
        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Specification<Event> paidEqualsTo(Boolean paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private Specification<Event> isOnlyAvailable(Boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.equal(root.get("participantLimit"), 0),
                criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")));
    }

    private Specification<Event> categoryIn(List<Long> categoriesIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id")).value(categoriesIds);
    }

    private Specification<Event> likeText(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("annotation")), text);
    }

    private Specification<Event> greaterThanOrEqualToRangeStart(String rangeStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), convertToDateTime(rangeStart));
    }

    private Specification<Event> lessThanOrEqualToRangeEnd(String rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), convertToDateTime(rangeEnd));
    }

    private Specification<Event> greaterThanTimeNow() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now());
    }

    private Specification<Event> equalsPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), PUBLISHED.name());
    }

    private Specification<Event> inUsers(List<Long> users) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id")).value(users);
    }

    private Specification<Event> inStates(List<String> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states);
    }
}

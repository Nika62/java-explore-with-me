package ru.practicum.ewm.service;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.Event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SpecificationEvent {

    private Specification<Event> nameLike(Boolean paid) {
        return new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return CriteriaBuilder.In(root.get(paid));
            }
        };
    }
}

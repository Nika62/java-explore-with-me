package ru.practicum.ewm.service.impl;

import ru.practicum.ewm.model.Event;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Event.class)
public interface Event_ {

    public static volatile SingularAttribute<Event, PAID> paid;
    public static volatile SingularAttribute<Distributor, String> name;
    public static volatile SingularAttribute<Distributor, String> id;
    public static final String PAID = "paid";
    public static final String NAME = "name";
    public static final String ID = "id";
}

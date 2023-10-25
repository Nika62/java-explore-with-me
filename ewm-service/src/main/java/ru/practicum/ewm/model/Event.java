package ru.practicum.ewm.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @JoinColumn(name = "confirmed_requests")
    private long confirmedRequests;
    //дата и время создания события
    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;
    @Column
    private String description;
    //время на которое намечено событие
    @JoinColumn(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;
    @JoinColumn(name = "location_lat")
    private float locationLat;
    @JoinColumn(name = "location_lon")
    private float locationLon;
    @Column
    private Boolean paid;
    // ограничение на кол-во участников
    @JoinColumn(name = "participant_limit")
    private int participantLimit;
    //дата публикации события
    @JoinColumn(name = "published_on")
    private LocalDateTime publishedOn;
    @JoinColumn(name = "request_moderation")
    private Boolean requestModeration;
    @Column
    private String state;
    @Column
    private String title;
    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();
}

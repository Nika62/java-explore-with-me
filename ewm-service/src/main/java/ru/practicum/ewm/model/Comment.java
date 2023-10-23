package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @JoinColumn(name = "createdOn")
    private LocalDateTime createdOn;

    public Comment(User user, String text, Event event, LocalDateTime createdOn) {
        this.user = user;
        this.text = text;
        this.event = event;
        this.createdOn = createdOn;
    }
}

package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reactions")
@Getter
@Setter
@NoArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "event_id")
    private Event event;
    @Column(nullable = false, name = "is_positive")
    private Boolean isPositive;

    public Reaction(Long userId, Long eventId) {
        User u = new User();
        u.setId(userId);
        this.user = u;
        Event e = new Event();
        e.setId(eventId);
        this.event = e;
    }
}
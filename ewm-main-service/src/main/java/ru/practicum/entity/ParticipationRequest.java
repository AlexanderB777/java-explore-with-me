package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private User requester;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(nullable = false, updatable = false)
    private Event event;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private LocalDateTime created;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private ParticipationRequestStatus status;

    public ParticipationRequest(Long requesterId,
                                Long eventId,
                                ParticipationRequestStatus status) {
        User user = new User();
        user.setId(requesterId);
        this.requester = user;
        Event event = new Event();
        event.setId(eventId);
        this.event = event;
        this.status = status;
    }
}
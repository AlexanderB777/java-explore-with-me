package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User initiator;
    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false, length = 2_000)
    private String annotation;
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long views;
    @Column(nullable = false, name = "event_date")
    private LocalDateTime eventDate;
    @Column(nullable = false, length = 7_000)
    private String description;
    @CreationTimestamp
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false, name = "created_on")
    private LocalDateTime createdOn;
    @Column(nullable = false, name = "participant_limit")
    private Integer participantLimit;
    @OneToMany(mappedBy = "event")
    @ToString.Exclude
    private List<ParticipationRequest> requests;

    @Column(nullable = false, name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
}
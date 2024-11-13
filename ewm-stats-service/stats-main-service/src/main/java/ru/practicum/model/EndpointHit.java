package ru.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name = "stats")
public class EndpointHit {
    @Id
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Reaction;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUser_idAndEvent_id(Long userId, Long eventId);
}
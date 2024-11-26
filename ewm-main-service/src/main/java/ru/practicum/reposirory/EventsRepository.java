package ru.practicum.reposirory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.Event;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id = :id AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiator(@Param("id") Long id,@Param("userId") Long userId);

    @Query("SELECT e.eventDate FROM Event e WHERE e.id = :id")
    LocalDateTime findEventDateById(@Param("id") Long id);

    @Query("SELECT e.state FROM Event e WHERE e.id = :id")
    EventState findStateById(@Param("id") Long id);

    @Query("SELECT e.requests FROM Event e " +
            "WHERE e.initiator.id = :userId AND e.id = :eventId")
    List<ParticipationRequest> findRequestsByUserIdAndEventId(@Param("userId") Long userId,
                                                              @Param("eventId") Long eventId);

    Page<Event> findAllByInitiatorId(Long initiatorId, PageRequest of);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id = :eventId")
    void incrementViewsById(Long eventId);
}
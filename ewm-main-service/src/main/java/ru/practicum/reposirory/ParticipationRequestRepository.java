package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    @Query("SELECT r FROM ParticipationRequest r " +
            "WHERE r.id = :id AND r.requester.id = :requesterId")
    Optional<ParticipationRequest> findByIdAndRequesterId(@Param("id") Long id,
                                                          @Param("requesterId") Long requesterId);

    @Query("SELECT COUNT(r) FROM ParticipationRequest r " +
            "WHERE r.event.id = :eventId " +
            "AND r.status = 'CONFIRMED'")
    Integer countByEventIdAndConfirmed(@Param("eventId") Long id);
}
package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.exception.EventConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequestStatus;
import ru.practicum.reposirory.EventsRepository;
import ru.practicum.reposirory.ParticipationRequestRepository;
import ru.practicum.reposirory.UserRepository;
import ru.practicum.service.RequestsService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {
    private final ParticipationRequestMapper mapper;
    private final ParticipationRequestRepository repository;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        log.info("service: getUsersRequests(), user id = {}", userId);
        return mapper.toDto(repository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto createUsersRequest(Long userId, Long eventId) {
        log.info("service: createUsersRequest(), user id = {}, eventId = {}", userId, eventId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("user with id=%d was not found", userId));
        }
        Event event = eventsRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s not found", eventId)));

        if (event.getRequests()
                .stream()
                .anyMatch(x -> x.getRequester().getId().equals(userId))) {
            throw new EventConstraintException("Нельзя оставить запрос 2 раза");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new EventConstraintException("Инициатор не может создавать запрос на участие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventConstraintException("Нельзя подать заявку на участие в неопубликованном событии");
        }
        int eventsParticipantLimit = event.getParticipantLimit();

        if (eventsParticipantLimit != 0
                && eventsParticipantLimit <= participationRequestRepository
                .countByEventIdAndConfirmed(event.getId())) {
            throw new EventConstraintException("Лимит заявок переполнен");
        }

        ParticipationRequest request = (!event.getRequestModeration() || event.getParticipantLimit() == 0)
                ? repository.save(new ParticipationRequest(userId, eventId, ParticipationRequestStatus.CONFIRMED))
                : repository.save(new ParticipationRequest(userId, eventId, ParticipationRequestStatus.PENDING));

        return mapper.toDto(repository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelUsersRequest(Long userId, Long requestId) {
        log.info("service: cancelUsersRequest(), user id = {}, requestId = {}", userId, requestId);
        ParticipationRequest request = repository
                .findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return mapper.toDto(repository.save(request));
    }
}
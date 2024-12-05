package ru.practicum.service.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.entity.*;
import ru.practicum.exception.EventConstraintException;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.*;
import ru.practicum.reposirory.EventsRepository;
import ru.practicum.reposirory.ReactionRepository;
import ru.practicum.reposirory.UserRepository;
import ru.practicum.service.EventsService;
import ru.practicum.stats.StatsClient;
import ru.practicum.util.UtilConstants;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {
    private final EventsRepository repository;
    private final EventMapper mapper;
    private final JPAQueryFactory queryFactory;
    private final StatsClient statsClient;
    private final ParticipationRequestMapper participationRequestMapper;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        log.info("Service: getUsersEvents(), userId = {}, from = {}, size = {}", userId, from, size);
        return mapper
                .toShortDto(repository
                        .findAllByInitiatorId(userId, PageRequest.of(from, size))
                        .getContent());
    }

    @Override
    public EventFullDto createUsersEvent(Long userId, NewEventDto dto) {
        log.info("Service: createUsersEvent(), userId = {}, dto = {}", userId, dto);
        if (LocalDateTime.parse(dto.eventDate(), UtilConstants.FORMATTER)
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. " +
                    "Value:" + dto.eventDate());
        }

        NewEventDto updatedDto = new NewEventDto(
                dto.annotation(),
                dto.category(),
                dto.description(),
                dto.eventDate(),
                dto.location(),
                dto.paid() != null ? dto.paid() : false,
                dto.participantLimit() != null ? dto.participantLimit() : 0,
                dto.requestModeration() != null ? dto.requestModeration() : true,
                dto.title()
        );

        Event event = repository.save(mapper.toEntity(userId, updatedDto));
        return mapper.toFullDto(event);
    }

    @Override
    public EventFullDto getUsersEventById(Long userId, Long eventId) {
        log.info("Service: getUsersEventById(), userId = {}, eventId = {}", userId, eventId);
        return mapper.toFullDto(repository
                .findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d was not found", eventId))));
    }

    @Override
    public EventFullDto updateUsersEventById(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Service: updateUsersEventById(), userId = {}, eventId = {}", userId, eventId);
        if (request.eventDate() != null
                && LocalDateTime.parse(request.eventDate(), UtilConstants.FORMATTER)
                .isBefore(LocalDateTime.now().minusHours(2L))) {
            throw new IncorrectRequestException("Can not set eventDate earlier than two hours after now");
        }


        Event event = repository
                .findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        switch (event.getState()) {
            case PENDING -> {
                if (request.stateAction() == StateAction.CANCEL_REVIEW) {
                    event.setState(EventState.CANCELED);
                }
            }
            case PUBLISHED -> throw new EventConstraintException("Only pending or canceled events can be changed");
            case CANCELED -> {
                if (request.stateAction() == StateAction.SEND_TO_REVIEW) {
                    event.setState(EventState.PENDING);
                }
            }
        }
        updateEventByQuery(request, event);
        return mapper.toFullDto(repository.save(event));
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        Integer from,
                                        Integer size) {
        log.info("Service: getEvents(), users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, " +
                "from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        QEvent event = QEvent.event;
        JPAQuery<Event> query = queryFactory.selectFrom(event);
        if (users != null && !users.isEmpty()) {
            query.where(event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            query.where(event.state.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            query.where(event.category.id.in(categories));
        }
        if (rangeStart != null && !rangeStart.isEmpty()) {
            query.where(event.eventDate.goe(LocalDateTime.parse(rangeStart, UtilConstants.FORMATTER)));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            query.where(event.eventDate.loe(LocalDateTime.parse(rangeEnd, UtilConstants.FORMATTER)));
        }
        query.offset(from).limit(size);
        List<Event> events = query.fetch();

        return mapper.toFullDto(events);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        log.info("Service: updateEvent(), eventId = {}, request = {}", eventId, request);
        if (request.eventDate() != null
                && LocalDateTime.parse(request.eventDate(), UtilConstants.FORMATTER)
                .isBefore(LocalDateTime.now())) {
            throw new IncorrectRequestException("Can not set eventDate after current date");
        }

        Event event = repository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        EventStateAction stateAction = request.stateAction();

        if (stateAction != null) {
            EventState currentState = event.getState();
            LocalDateTime eventDateTime;
            if (request.eventDate() != null) {
                eventDateTime = LocalDateTime.parse(request.eventDate(), UtilConstants.FORMATTER);
            } else {
                eventDateTime = event.getEventDate();
            }
            if (stateAction == EventStateAction.REJECT_EVENT) {
                if (currentState != EventState.PENDING) {
                    throw new EventConstraintException("Only pending events can be canceled");
                } else {
                    event.setState(EventState.CANCELED);
                }
            } else if (stateAction == EventStateAction.PUBLISH_EVENT) {
                if (eventDateTime.isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new EventConstraintException("Cannot publish event after 1 hour to event.");
                }
                if (currentState != EventState.PENDING) {
                    throw new EventConstraintException("Cannot publish event " +
                            "because it is not in the right state: PENDING");
                } else {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            }
        }

        updateEventByQuery(request, event);
        return mapper.toFullDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size,
                                               String remoteAddr,
                                               String requestURI) {
        log.info("Service: getPublicEvents(), text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, " +
                        "onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        statsClient.recordHit("ewm-main-service",
                requestURI,
                remoteAddr,
                LocalDateTime.now());
        QEvent event = QEvent.event;
        JPAQuery<Event> query = queryFactory.selectFrom(event);
        query.where(event.state.eq(EventState.PUBLISHED));
        if (text != null && !text.isEmpty()) {
            query.where(event.annotation.containsIgnoreCase(text)
                    .or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            query.where(event.category.id.in(categories));
        }
        if (paid != null) {
            query.where(event.paid.eq(paid));
        }

        patchQueryWithRangeTime(rangeStart, rangeEnd, query, event);

        if (onlyAvailable) {
            query.where(event.participantLimit.gt(event.requests.size()));
        }
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "EVENT_DATE" -> query.orderBy(event.eventDate.desc());

                case "VIEWS" -> query.orderBy(event.views.desc());

            }
        }
        query.offset(from).limit(size);
        return mapper.toShortDto(query.fetch());
    }

    @Override
    public EventFullDto getEvent(Long id, String remoteAddr, String requestURI) {
        log.info("Service: getEvent(), id = {}", id);
        statsClient.recordHit("ewm-main-service",
                requestURI,
                remoteAddr,
                LocalDateTime.now());
        QEvent event = QEvent.event;
        JPAQuery<Event> query = queryFactory.selectFrom(event);
        query.where(event.id.eq(id));
        query.where(event.state.eq(EventState.PUBLISHED));
        Event eventEntity = Optional
                .ofNullable(query.fetchOne())
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        Long uniqueViews = 0L;
        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now(),
                Collections.singletonList(requestURI),
                true);
        log.info("GettingStats. {}", viewStatsDtos);
        if (viewStatsDtos.size() == 1) {
            ViewStatsDto viewStatsDto = viewStatsDtos.getFirst();
            uniqueViews = viewStatsDto.hits();
        }
        eventEntity.setViews(uniqueViews);
        return mapper.toFullDto(repository.save(eventEntity));
    }

    @Override
    public List<ParticipationRequestDto> getUsersParticipationRequests(Long userId, Long eventId) {
        log.info("service: getUsersParticipationRequests(), userId: {}, eventId: {}", userId, eventId);
        List<ParticipationRequest> requests = repository.findRequestsByUserIdAndEventId(userId, eventId);
        return participationRequestMapper.toDto(requests);
    }

    private EventShortDto putLike(Long userId, Long eventId) {
        log.info("service: putLike(), userId: {}, eventId: {}", userId, eventId);
        checkUserAndEvent(userId, eventId);
        if (!checkPublishedById(eventId)) {
            throw new IncorrectRequestException("Event with id " + eventId + " is not published");
        }
        Reaction reaction = reactionRepository
                .findByUser_idAndEvent_id(userId, eventId)
                .orElse(new Reaction(userId, eventId));
        reaction.setIsPositive(true);
        reactionRepository.save(reaction);
        return mapper.toShortDto(repository.findById(eventId).orElse(null));
    }

    private EventShortDto putDislike(Long userId, Long eventId) {
        log.info("service: putDislike(), userId: {}, eventId: {}", userId, eventId);
        checkUserAndEvent(userId, eventId);
        if (!checkPublishedById(eventId)) {
            throw new IncorrectRequestException("Event with id " + eventId + " is not published");
        }
        Reaction reaction = reactionRepository
                .findByUser_idAndEvent_id(userId, eventId)
                .orElse(new Reaction(userId, eventId));
        reaction.setIsPositive(false);
        reactionRepository.save(reaction);
        return mapper.toShortDto(repository.findById(eventId).orElse(null));
    }

    private boolean checkPublishedById(Long eventId) {
        Event event = repository.findById(eventId).orElse(null);
        return event != null && event.getState() == EventState.PUBLISHED;
    }

    private void deleteLike(Long userId, Long eventId) {
        log.info("service: deleteLike(), userId: {}, eventId: {}", userId, eventId);
        checkUserAndEvent(userId, eventId);
        Optional<Reaction> reaction = reactionRepository.findByUser_idAndEvent_id(userId, eventId);
        if (reaction.isPresent() && reaction.get().getIsPositive()) {
            reactionRepository.deleteById(reaction.get().getId());
        }
    }

    private void deleteDislike(Long userId, Long eventId) {
        log.info("service: deleteDislike(), userId: {}, eventId: {}", userId, eventId);
        checkUserAndEvent(userId, eventId);
        Optional<Reaction> reaction = reactionRepository.findByUser_idAndEvent_id(userId, eventId);
        if (reaction.isPresent() && !reaction.get().getIsPositive()) {
            reactionRepository.deleteById(reaction.get().getId());
        }
    }

    @Override
    public List<EventFullDto> getEventsByRating() {
        log.info("service: getEventsByRating()");
        List<Event> events = repository.findAllByEventState(EventState.PUBLISHED);
        return events
                .stream()
                .map(mapper::toFullDto)
                .sorted(Comparator.comparing(x -> x.likes() - x.dislikes()))
                .toList()
                .reversed();
    }

    @Override
    public EventShortDto putReaction(Long userId, Long eventId, Boolean isPositive) {
        return isPositive
                ? putLike(userId, eventId)
                : putDislike(userId, eventId);
    }

    @Override
    public void deleteReaction(Long userId, Long eventId, Boolean isPositive) {
        if (isPositive) {
            deleteLike(userId, eventId);
        } else {
            deleteDislike(userId, eventId);
        }
    }

    @Override
    public EventRequestStatusUpdateResult updateUsersParticipationRequest(Long userId,
                                                                          Long eventId,
                                                                          EventRequestStatusUpdateRequest request) {
        log.info("service: updateUsersParticipationRequest(), userId: {}, eventId: {}", userId, eventId);
        Event event = repository
                .findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return null;
        }
        int participantRemains = countParticipantRemains(event);
        if (participantRemains <= 0) {
            throw new EventConstraintException("Participant limit reached");
        }

        List<ParticipationRequest> requestsForUpdateStatus = event.getRequests()
                .stream()
                .filter(participationRequest -> request.requestIds().contains(participationRequest.getId()))
                .filter(participationRequest -> {
                    if (participationRequest.getStatus() != ParticipationRequestStatus.PENDING) {
                        throw new IncorrectRequestException("Request must have status PENDING");
                    }
                    return true;
                })
                .toList();
        ParticipationRequestStatus statusToSet =
                request.status() == RequestStatus.CONFIRMED
                        ? ParticipationRequestStatus.CONFIRMED
                        : ParticipationRequestStatus.REJECTED;

        for (ParticipationRequest participationRequest : requestsForUpdateStatus) {
            participationRequest
                    .setStatus(participantRemains-- > 0
                            ? statusToSet
                            : ParticipationRequestStatus.REJECTED);
        }

        repository.save(event);
        return new EventRequestStatusUpdateResult(
                participationRequestMapper.toDto(requestsForUpdateStatus
                        .stream()
                        .filter(x -> x.getStatus() == ParticipationRequestStatus.CONFIRMED)
                        .toList()),
                participationRequestMapper.toDto(requestsForUpdateStatus
                        .stream()
                        .filter(x -> x.getStatus() == ParticipationRequestStatus.REJECTED)
                        .toList()));
    }

    private void updateEventByQuery(UpdateEventUserRequest request, Event event) {
        updateEventByQuery(event,
                request.annotation(),
                request.category(),
                request.description(),
                request.eventDate(),
                request.location(),
                request.paid(),
                request.participantLimit(),
                request.requestModeration(),
                request.title());
    }

    private void updateEventByQuery(UpdateEventAdminRequest request, Event event) {
        updateEventByQuery(event,
                request.annotation(),
                request.category(),
                request.description(),
                request.eventDate(),
                request.location(),
                request.paid(),
                request.participantLimit(),
                request.requestModeration(),
                request.title());
    }

    private void updateEventByQuery(Event event,
                                    String annotation,
                                    Long category,
                                    String description,
                                    String eventDate,
                                    Location location,
                                    Boolean paid,
                                    Integer participantLimit,
                                    Boolean requestModeration,
                                    String title) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(new Category());
            event.getCategory().setId(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, UtilConstants.FORMATTER));
        }
        if (location != null) {
            event.setLat(location.lat());
            event.setLon(location.lon());
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }

    private int countParticipantRemains(Event event) {
        if (event.getParticipantLimit() == 0) {
            return event.getRequests().size();
        } else {
            long limitCount = event.getParticipantLimit();
            long confirmedCount = event.getRequests()
                    .stream()
                    .filter(request -> request.getStatus() == ParticipationRequestStatus.CONFIRMED)
                    .count();
            return Math.toIntExact(limitCount - confirmedCount);
        }
    }

    private void patchQueryWithRangeTime(String rangeStart, String rangeEnd, JPAQuery<Event> query, QEvent event) {
        LocalDateTime startTime = rangeStart != null ? LocalDateTime.parse(rangeStart, UtilConstants.FORMATTER) : null;
        LocalDateTime endTime = rangeEnd != null ? LocalDateTime.parse(rangeEnd, UtilConstants.FORMATTER) : null;

        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new IncorrectRequestException("Start time must be before end time");
        }

        if (startTime == null && endTime == null) {
            query.where(event.eventDate.goe(LocalDateTime.now()));
        } else {
            if (startTime != null) {
                query.where(event.eventDate.goe(startTime));
            }
            if (endTime != null) {
                query.where(event.eventDate.loe(endTime));
            }
        }
    }

    private void checkUserAndEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " does not exist");
        }
        if (!repository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " does not exist");
        }
    }
}
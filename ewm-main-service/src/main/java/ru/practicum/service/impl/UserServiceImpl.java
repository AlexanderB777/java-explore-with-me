package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.AuthorDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.Reaction;
import ru.practicum.entity.User;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.EventState;
import ru.practicum.reposirory.EventsRepository;
import ru.practicum.reposirory.ReactionRepository;
import ru.practicum.reposirory.UserRepository;
import ru.practicum.service.UserService;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final EventsRepository eventsRepository;
    private final UserMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Service: getUsers(), ids={}, from={}, size={}", ids, from, size);
        return mapper.toDto(repository
                .findByIdIn(ids, PageRequest.of(from, size))
                .getContent());
    }

    @Override
    public UserDto createUser(NewUserRequest request) {
        log.info("Service: createUser(), request={}", request);
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Service: deleteUser(), userId={}", userId);
        repository.deleteById(userId);
    }

    @Override
    public List<AuthorDto> getAuthorsByRating() {
        log.info("Service: getAuthorsByRating()");
        List<Event> events = eventsRepository.findAllByEventState(EventState.PUBLISHED);
        Map<User, Long> userRatings = events.stream()
                .collect(Collectors.groupingBy(
                        Event::getInitiator,
                        Collectors.summingLong(event ->
                                event.getReactions().stream()
                                        .mapToLong(reaction -> reaction.getIsPositive() ? 1 : -1)
                                        .sum()
                        )
                ));
        return userRatings
                .entrySet()
                .stream()
                .map(x -> new AuthorDto(
                        x.getKey().getId(),
                        x.getKey().getName(),
                        x.getValue()
                ))
                .sorted(Comparator.comparing(AuthorDto::rating).reversed())
                .toList();
    }
}
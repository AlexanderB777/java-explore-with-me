package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.reposirory.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
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
}
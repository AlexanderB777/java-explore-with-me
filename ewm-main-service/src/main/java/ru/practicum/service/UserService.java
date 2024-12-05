package ru.practicum.service;

import ru.practicum.dto.user.AuthorDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(NewUserRequest request);

    void deleteUser(Long userId);

    List<AuthorDto> getAuthorsByRating();
}
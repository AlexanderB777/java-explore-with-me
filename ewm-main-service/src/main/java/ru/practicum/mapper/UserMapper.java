package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> toDto(List<User> users);

    UserDto toDto(User user);

    User toEntity(NewUserRequest dto);

    UserShortDto toShortDto(User user);
}
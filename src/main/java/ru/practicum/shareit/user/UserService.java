package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(long id);

    UserDto saveUser(UserDto user);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}

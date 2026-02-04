package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getUsers();

    public UserDto getUserById(long id);

    public UserDto saveUser(UserDto user);

    public UserDto updateUser(Long userId, UserDto userDto);

    public void deleteUser(Long userId);
}

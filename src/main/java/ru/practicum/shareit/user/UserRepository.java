package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User saveUser(UserDto user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}

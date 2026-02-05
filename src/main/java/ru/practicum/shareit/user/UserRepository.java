package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User saveUser(UserDtoRequest userDtoRequest);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

}

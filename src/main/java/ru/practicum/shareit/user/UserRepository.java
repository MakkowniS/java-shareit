package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public List<User> getUsers();

    public Optional<User> getUserById(Long id);

    public Optional<User> getUserByEmail(String email);

    public User saveUser(UserDto user);

    public User updateUser(Long userId, User user);

    public void deleteUser(Long userId);
}

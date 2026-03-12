package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;

public interface UserService {
    List<UserDtoResponse> getUsers();

    UserDtoResponse getUserById(long id);

    UserDtoResponse saveUser(UserDtoRequest userDtoRequest);

    UserDtoResponse updateUser(Long userId, UserDtoRequest userDtoRequest);

    UserDtoResponse deleteUser(Long userId);
}

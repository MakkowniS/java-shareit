package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDtoResponse mapToUserDto(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapDtoRequestToUser(UserDtoRequest userDtoRequest) {
        User user = new User();
        user.setName(userDtoRequest.getName());
        user.setEmail(userDtoRequest.getEmail());
        return user;
    }

}

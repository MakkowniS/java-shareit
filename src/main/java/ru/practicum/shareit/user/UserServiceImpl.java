package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.DuplicateDataException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDtoResponse> getUsers() {
        return userRepository.getUsers().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDtoResponse getUserById(long id) {
        return userRepository.getUserById(id).map(UserMapper::mapToUserDto).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public UserDtoResponse saveUser(UserDtoRequest userDtoRequest) {
        isEmailExists(userDtoRequest);
        return UserMapper.mapToUserDto(userRepository.saveUser(userDtoRequest));
    }

    @Override
    public UserDtoResponse updateUser(Long userId, UserDtoRequest userDtoRequest) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDtoRequest.getEmail() != null && !userDtoRequest.getEmail().equals(user.getEmail())) {

            isEmailExists(userDtoRequest);
            user.setEmail(userDtoRequest.getEmail());
        }

        if (userDtoRequest.getName() != null) {
            user.setName(userDtoRequest.getName());
        }

        return UserMapper.mapToUserDto(userRepository.updateUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private void isEmailExists(UserDtoRequest userDtoRequest) {
        userRepository.getUserByEmail(userDtoRequest.getEmail()).ifPresent(u -> {
            throw new DuplicateDataException("Данный Email уже используется");
        });
    }

}

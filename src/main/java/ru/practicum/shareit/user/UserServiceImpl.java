package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
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
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDtoResponse getUserById(long id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public UserDtoResponse saveUser(UserDtoRequest dto) {
        isEmailExists(dto);

        User newUser = UserMapper.mapDtoRequestToUser(dto);

        return UserMapper.mapToUserDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public UserDtoResponse updateUser(Long userId, UserDtoRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            isEmailExists(dto);
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void isEmailExists(UserDtoRequest userDtoRequest) {
        userRepository.findUserByEmail(userDtoRequest.getEmail()).ifPresent(u -> {
            throw new DuplicateDataException("Данный Email уже используется");
        });
    }

}

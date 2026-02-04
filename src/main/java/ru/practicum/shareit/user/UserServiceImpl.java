package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        return userRepository.getUserById(id).map(userMapper::toUserDto).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        isEmailExists(userDto);
        return userMapper.toUserDto(userRepository.saveUser(userDto));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {

            isEmailExists(userDto);
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userMapper.toUserDto(userRepository.updateUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private void isEmailExists(UserDto userDto) {
        userRepository.getUserByEmail(userDto.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Данный Email уже используется");
        });
    }

}

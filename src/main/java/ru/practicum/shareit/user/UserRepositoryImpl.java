package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> USERS = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(USERS.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(USERS.get(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(USERS.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null));
    }

    @Override
    public User saveUser(UserDtoRequest userDtoRequest) {
        Long id = getNextId();
        User newUser = UserMapper.mapDtoRequestToUser(userDtoRequest);
        newUser.setId(id);

        USERS.put(id, newUser);
        return newUser;
    }

    @Override
    public User updateUser(Long userId, User user) {
        USERS.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        USERS.remove(userId);
    }

    private Long getNextId() {
        long currentId = USERS.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}

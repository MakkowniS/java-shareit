package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получение пользователей.");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        log.info("Получение пользователя с ID={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        log.info("Создание пользователя {}", userDtoRequest);
        return userClient.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable Long userId,
                                             @RequestBody UserDtoRequest userDtoRequest) {
        log.info("Изменение пользователя с ID={}, {}", userId, userDtoRequest);
        return userClient.updateUser(userId, userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable Long userId) {
        log.info("Удаление пользователя с ID={}", userId);
        return userClient.deleteUser(userId);
    }

}

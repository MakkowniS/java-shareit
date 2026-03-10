package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        return userClient.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Positive @PathVariable Long userId,
                                             @RequestBody UserDtoRequest userDtoRequest) {
        return userClient.updateUser(userId, userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }

}

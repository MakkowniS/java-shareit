package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDtoResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDtoResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDtoResponse createUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        return userService.saveUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public UserDtoResponse updateUser(@PathVariable Long userId, @RequestBody UserDtoRequest userDtoRequest) {
        return userService.updateUser(userId, userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}

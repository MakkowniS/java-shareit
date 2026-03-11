package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.DuplicateDataException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository; // Создаём мок репозитория

    @InjectMocks
    private UserServiceImpl userService; // Создаём сервис и вставляем мок

    private User user;
    private UserDtoRequest userDtoRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("max@mail.ru");
        user.setName("Max");
        userDtoRequest = new UserDtoRequest("Max", "max@mail.ru");
    }

    @Test
    void saveUser_ifEmailUnique_shouldSaveUser() {
        // Указываем что email уникален
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDtoResponse result = userService.saveUser(userDtoRequest);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_ifEmailExists_shouldThrowDuplicateDataException() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(DuplicateDataException.class, () -> userService.saveUser(userDtoRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDtoResponse result = userService.getUserById(1L);

        assertEquals("Max", result.getName());
    }

    @Test
    void getUsers_whenUserExists_shouldReturnUsersList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDtoResponse> result = userService.getUsers();

        assertEquals(user.getEmail(), result.getFirst().getEmail());
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(10L));
    }

    @Test
    void updateUser_shouldUpdateNonNullFields(){
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("old");
        oldUser.setEmail("old@mail.ru");
        UserDtoRequest updateDto = new UserDtoRequest("New name", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDtoResponse result = userService.updateUser(1L, updateDto);

        assertEquals("New name", result.getName());
        assertEquals("old@mail.ru", result.getEmail());
    }
}

package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDtoResponse userDtoResponse;

    @BeforeEach
    void setUp() {
        userDtoResponse = new UserDtoResponse(1L,"User", "user@mail.com");
    }

    @Test
    void getUsers_shouldReturnList() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(userDtoResponse));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoResponse.getName())));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDtoResponse);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDtoRequest request = new UserDtoRequest("User", "user@mail.ru");
        when(userService.saveUser(any())).thenReturn(userDtoResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("User")));

        verify(userService, times(1)).saveUser(request);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDtoRequest updateRequest = new UserDtoRequest("NewName", null);
        userDtoResponse.setName("NewName");

        when(userService.updateUser(anyLong(), any())).thenReturn(userDtoResponse);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NewName")));

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @Test
    void deleteUser_shouldReturnDeletedUser() throws Exception {
        when(userService.deleteUser(anyLong())).thenReturn(userDtoResponse);

        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(userService, times(1)).deleteUser(1L);
    }

}

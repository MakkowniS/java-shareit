package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDtoRequest {
    @NotBlank
    String name;

    @NotBlank
    @Email
    String email;
}

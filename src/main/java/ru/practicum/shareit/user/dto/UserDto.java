package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private final Long id;
    @NotNull
    @NotBlank
    private final String name;
    @Email
    private final String email;
}

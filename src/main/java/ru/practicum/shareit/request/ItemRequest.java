package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private User requestor;
    LocalDateTime created = LocalDateTime.now();
}

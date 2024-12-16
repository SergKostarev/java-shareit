package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemDto {
    private final Long id;
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    @NotBlank
    private final String description;
    @NotNull
    private final Boolean available;
    private final Long requestId;
}

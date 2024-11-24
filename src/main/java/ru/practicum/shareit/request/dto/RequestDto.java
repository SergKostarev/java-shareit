package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private final Long id;
    @NotNull
    @NotBlank
    private final String description;
    @NotNull
    private final Long requestor;
    private final LocalDateTime created;

}

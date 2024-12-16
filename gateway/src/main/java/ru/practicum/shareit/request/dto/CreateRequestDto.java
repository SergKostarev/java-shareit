package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequestDto {
    private Long id;
    @NotNull
    @NotBlank
    private String description;
}
package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class CreateRequestDto {
    private final Long id;
    private final String description;
}
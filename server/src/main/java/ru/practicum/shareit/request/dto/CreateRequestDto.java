package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class CreateRequestDto {
    private Long id;
    private String description;
}
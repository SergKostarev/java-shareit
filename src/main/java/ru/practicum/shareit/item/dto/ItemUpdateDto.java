package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long request;
}

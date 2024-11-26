package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemRequestDto {
    private final Long id;
    private final String name;
    private final Long ownerId;
}

package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestItemDto {
    private final Long id;
    private final String description;
    private final Long requestorId;
    private final LocalDateTime created;
    private final List<ItemRequestDto> items;
}

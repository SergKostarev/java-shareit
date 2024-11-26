package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemCommentDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long requestId;
    private final LocalDateTime lastBooking;
    private final LocalDateTime nextBooking;
    private final List<CommentDto> comments;
}

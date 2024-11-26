package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CreateCommentDto {
    private final Long id;
    private final String text;
}

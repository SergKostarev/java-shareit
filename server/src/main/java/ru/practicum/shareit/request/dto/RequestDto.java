package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private final Long id;
    private final String description;
    private final Long requestor;
    private final LocalDateTime created;

}

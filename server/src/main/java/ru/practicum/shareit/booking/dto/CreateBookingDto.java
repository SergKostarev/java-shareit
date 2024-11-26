package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;
}

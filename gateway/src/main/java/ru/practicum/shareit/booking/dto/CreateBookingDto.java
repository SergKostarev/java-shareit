package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingDto {
    private final Long id;
    @NotNull
    private final LocalDateTime start;
    @NotNull
    private final LocalDateTime end;
    @NotNull
    private final Long itemId;
}
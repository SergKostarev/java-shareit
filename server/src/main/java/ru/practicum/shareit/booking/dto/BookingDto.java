package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemBookingDto item;
    private final UserBookingDto booker;
    private final BookingStatus status;
}

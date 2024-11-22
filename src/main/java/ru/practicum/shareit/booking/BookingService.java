package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, CreateBookingDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String stateString);

    List<BookingDto> getItemOwnerBookings(Long ownerId, String stateString);
}

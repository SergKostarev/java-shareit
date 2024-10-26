package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Data
public class Booking {
    private Long id;
    @NotNull
    @Future
    private LocalDateTime start; // TODO check start is prior to end
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    BookingStatus status = WAITING;
}

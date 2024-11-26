package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableEntityException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;
    private final BookingService service;

    private final LocalDateTime start = LocalDateTime.of(2025, 1, 1, 23, 58, 00);
    private final LocalDateTime end = LocalDateTime.of(2025, 1, 1, 23, 59, 00);

    @Test
    void createBooking() {
        CreateBookingDto createBookingDto = makeCreateBookingDto(4L);
        service.create(1L, createBookingDto);
        Booking booking = em.find(Booking.class,4L);
        User booker = em.find(User.class,1L);
        Item item = em.find(Item.class,4L);
        Assertions.assertThat(booking)
                .isInstanceOf(Booking.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
    }

    @Test
    void givenIncorectTime_shouldNotCreateBooking() {
        CreateBookingDto createBookingDto = makeCreateBookingDto(end, start, 4L);
        assertThatThrownBy(() -> {service.create(1L, createBookingDto);
            }).isInstanceOf(IncorrectDataException.class);
    }

    @Test
    void givenUnavailableItem_shouldNotCreateBooking() {
        CreateBookingDto createBookingDto = makeCreateBookingDto(3L);
        assertThatThrownBy(() -> {service.create(1L, createBookingDto);
            }).isInstanceOf(UnavailableEntityException.class);
    }

    @Test
    void approveBooking() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 29, 23, 59, 59);
        LocalDateTime end = LocalDateTime.of(2025, 12, 30, 23, 59, 59);
        service.approve(2L, 3L, true);
        Booking booking = em.find(Booking.class,3L);
        User booker = em.find(User.class,1L);
        Item item = em.find(Item.class,4L);
        Assertions.assertThat(booking)
                .isInstanceOf(Booking.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
    }

    @Test
    void givenNonExistentBooking_shouldNotUpdateBooking() {
        assertThatThrownBy(() -> {service.approve(2L, 10L, true);
            }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void givenNotOwnerUser_shouldNotUpdateBooking() {
        assertThatThrownBy(() -> {service.approve(1L, 3L, true);
            }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    void givenNonExistentBooking_shouldNotGetBooking() {
        assertThatThrownBy(() -> {service.getById(1L, 10L);
            }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void givenNotOwnerUser_shouldNotGetBooking() {
        assertThatThrownBy(() -> {service.getById(3L, 3L);
            }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    void getById() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 29, 23, 59, 59);
        LocalDateTime end = LocalDateTime.of(2025, 12, 30, 23, 59, 59);
        ItemBookingDto itemBookingDto = new ItemBookingDto(4L, "Тестовая вещь");
        UserBookingDto userBookingDto = new UserBookingDto(1L);
        BookingDto booking = service.getById(1L, 3L);
        Assertions.assertThat(booking)
                .isInstanceOf(BookingDto.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("item", itemBookingDto)
                .hasFieldOrPropertyWithValue("booker", userBookingDto)
                .hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
    }

    @Test
    void getAllBookings() {
        Assertions.assertThat(
                service.getUserBookings(1L, BookingState.ALL)).hasSize(3);
    }

    @Test
    void getPastBookings() {
        Assertions.assertThat(
                service.getUserBookings(1L, BookingState.PAST)).hasSize(2);
    }

    @Test
    void getWaitingBookings() {
        Assertions.assertThat(
                service.getUserBookings(1L, BookingState.WAITING)).hasSize(1);
    }

    @Test
    void getItemOwnerAllBookings() {
        Assertions.assertThat(
                service.getItemOwnerBookings(2L, BookingState.ALL)).hasSize(3);
    }

    @Test
    void getItemOwnerPastBookings() {
        Assertions.assertThat(
                service.getItemOwnerBookings(2L, BookingState.PAST)).hasSize(2);
    }

    @Test
    void getItemOwnerWaitingBookings() {
        Assertions.assertThat(
                service.getItemOwnerBookings(2L, BookingState.WAITING)).hasSize(1);
    }

    @Test
    void givenAlreadyTakenTime_shouldNotCreateBooking() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 30, 21, 55, 00);
        LocalDateTime end = LocalDateTime.of(2025, 12, 30, 23, 55, 00);
        CreateBookingDto createBookingDto = makeCreateBookingDto(start, end,4L);
        assertThatThrownBy(() -> {service.create(1L, createBookingDto);
            }).isInstanceOf(UnavailableEntityException.class);
    }

    CreateBookingDto makeCreateBookingDto(Long itemId) {
        return new CreateBookingDto(null, start, end, itemId);
    }
    CreateBookingDto makeCreateBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return new CreateBookingDto(null, start, end, itemId);
    }
}

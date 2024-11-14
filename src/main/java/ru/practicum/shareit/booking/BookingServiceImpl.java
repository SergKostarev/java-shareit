package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableEntityException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.shareit.booking.BookingState.getState;
import static ru.practicum.shareit.booking.BookingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService{

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional()
    public BookingDto create(Long userId, CreateBookingDto bookingDto) {
        log.debug("Создание нового бронирования");
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start) || end.isEqual(start)
              //|| !end.isAfter(LocalDateTime.now()) || !start.isAfter(LocalDateTime.now())
        ) {
            throw new IncorrectDataException("Некорректные сроки бронирования",
                    bookingDto.getEnd().toString());
        }
        User booker = userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new UnavailableEntityException("Вещь недоступна ",
                    item.getId().toString());
        }
        Booking activeBooking = bookingRepository.
                findByItemIdAndEndIsAfterAndStartIsBeforeAndStatusIn(
                        bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd(),
                        Set.of(WAITING, APPROVED));

        if (activeBooking != null) {
            throw new UnavailableEntityException("Бронирование на заданное время уже существует",
                    bookingDto.getEnd().toString());
        }
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional()
    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        log.debug("Подтверждение бронирования");
        try {
            userRepository.getUserById(userId);
        } catch (NotFoundException e) {
            throw new NotAuthorizedException(e.getMessage(),
                    e.getId(), bookingId);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено.", bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.debug("Пользователь с идентификатором " + userId +
                    " не является владельцем вещи с идентификатором " + booking.getItem().getId()
                    + ", подтверждение бронирования невозможно");
            throw new NotAuthorizedException("Недопустимая для пользователя операция",
                    userId, booking.getItem().getId());
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        log.debug("Получение бронирования с идентификатором " + bookingId);
        userRepository.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено.", bookingId));
        if (!(booking.getItem().getOwner().getId().equals(userId)
                || booking.getBooker().getId().equals(userId))) {
            log.debug("Пользователь с идентификатором " + userId
                    + " не является владельцем вещи с идентификатором " + booking.getItem().getId()
                    + " или создателем бронирования с идентификатором " + booking.getId()
                    + ", получение бронирования невозможно");
            throw new NotAuthorizedException("Недопустимая для пользователя операция",
                    userId, booking.getItem().getId());
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateString) {
        userRepository.getUserById(userId);
        Sort sort = Sort.by("end").descending();
        LocalDateTime now = LocalDateTime.now();
        BookingState state = getState(stateString);
        List<Booking> bookings;
        switch (state) {
            case CURRENT -> bookings = bookingRepository.
                    findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
            case PAST -> bookings = bookingRepository
                    .findByBookerIdAndEndIsBefore(userId, now, sort);
            case FUTURE -> bookings = bookingRepository
                    .findByBookerIdAndStartIsAfter(userId, now, sort);
            case WAITING -> bookings = bookingRepository
                    .findByBookerIdAndStatus(userId, WAITING.ordinal(), sort);
            case REJECTED -> bookings = bookingRepository
                    .findByBookerIdAndStatus(userId, REJECTED.ordinal(), sort);
            default -> bookings = bookingRepository.findByBookerId(userId, sort);
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getItemOwnerBookings(Long ownerId, String stateString) {
        userRepository.getUserById(ownerId);
        Sort sort = Sort.by("end").descending();
        LocalDateTime now = LocalDateTime.now();
        BookingState state = getState(stateString);
        List<Booking> bookings;
        switch (state) {
            case CURRENT -> bookings = bookingRepository.
                    findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, now, now, sort);
            case PAST -> bookings = bookingRepository
                    .findByItemOwnerIdAndEndIsBefore(ownerId, now, sort);
            case FUTURE -> bookings = bookingRepository
                    .findByItemOwnerIdAndStartIsAfter(ownerId, now, sort);
            case WAITING -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatus(ownerId, WAITING.ordinal(), sort);
            case REJECTED -> bookings = bookingRepository
                    .findByItemOwnerIdAndStatus(ownerId, REJECTED.ordinal(), sort);
            default -> bookings = bookingRepository.findByItemOwnerId(ownerId, sort);
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}

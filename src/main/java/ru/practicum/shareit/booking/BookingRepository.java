package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    default Booking getBookingById(Long bookingId) {
        return findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено.", bookingId));
    }

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Integer status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemIdIn(Set<Long> itemId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, Integer status, Sort sort);

    Booking findByItemIdAndEndIsAfterAndStartIsBeforeAndStatusIn(
            Long itemId, LocalDateTime end, LocalDateTime start, Set<BookingStatus> status);

    List<Booking> findByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    List<Booking> findByItemIdAndStartIsAfter(Long itemId, LocalDateTime start);

    List<Booking> findByItemIdAndStartIsBefore(Long itemId, LocalDateTime end);

}

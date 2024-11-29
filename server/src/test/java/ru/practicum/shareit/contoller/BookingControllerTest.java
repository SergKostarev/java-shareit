package ru.practicum.shareit.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableEntityException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.of(2025, 12, 29, 23, 59, 59),
            LocalDateTime.of(2025, 12, 30, 23, 59, 59),
            new ItemBookingDto(4L, "Тестовая вещь"),
            new UserBookingDto(1L), BookingStatus.WAITING);
    BookingDto bookingDto2 = new BookingDto(2L,
            LocalDateTime.of(2024, 12, 29, 23, 59, 59),
            LocalDateTime.of(2024, 12, 30, 23, 59, 59),
            new ItemBookingDto(5L, "Тестовая вещь 2"),
            new UserBookingDto(2L), BookingStatus.WAITING);

    @Test
    void createBooking() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void givenIncorrectDataExceptionInCreateBooking_statusIsBadRequest() throws Exception {
        when(bookingService.create(any(), any()))
                .thenThrow(IncorrectDataException.class);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenUnavailableEntityExceptionInCreateBooking_statusIsBadRequest() throws Exception {
        when(bookingService.create(any(), any()))
                .thenThrow(UnavailableEntityException.class);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(any(), any(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void givenApproveNotFoundExceptionInApprove_statusIsFound() throws Exception {
        when(bookingService.approve(any(), any(), anyBoolean()))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNotAuthorizedExceptionInApprove_statusIsForbidden() throws Exception {
        when(bookingService.approve(any(), any(), anyBoolean()))
                .thenThrow(NotAuthorizedException.class);
        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/{bookingId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void givenGetByIdNotFoundExceptionInGetById_statusIsFound() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(any(), any()))
                .thenReturn(Arrays.asList(bookingDto, bookingDto2));
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                        content().json(mapper.writeValueAsString(Arrays.asList(bookingDto, bookingDto2))));
    }

    @Test
    void getItemOwnerBookings() throws Exception {
        when(bookingService.getItemOwnerBookings(any(), any()))
                .thenReturn(Arrays.asList(bookingDto, bookingDto2));
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(
                        content().json(mapper.writeValueAsString(Arrays.asList(bookingDto, bookingDto2))));
    }

}

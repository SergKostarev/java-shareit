package ru.practicum.shareit.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private RequestDto requestDto = new RequestDto(1L,"Описание",1L,
            LocalDateTime.of(2024, 1, 1, 23, 59, 05));

    @Test
    void createRequest() throws Exception {
        when(requestService.create(any(), any()))
                .thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(requestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString()), LocalDateTime.class));
    }

    @Test
    void getRequests() throws Exception {
        RequestItemDto request1 = new RequestItemDto(1L, "Запрос 1", 1L,
                LocalDateTime.of(2024, 1, 1, 23, 59, 05),
                new ArrayList<>());
        RequestItemDto request2 = new RequestItemDto(1L, "Запрос 2", 1L,
                LocalDateTime.of(2024, 1, 1, 23, 59, 10),
                new ArrayList<>());
        when(requestService.getRequests(any())).thenReturn(Arrays.asList(request1, request2));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andExpect(
                                content().json(mapper.writeValueAsString(Arrays.asList(request1, request2))));
    }

    @Test
    void getRequest() throws Exception {
        RequestItemDto request = new RequestItemDto(1L, "Запрос 1", 1L,
                LocalDateTime.of(2024, 1, 1, 23, 59, 05),
                new ArrayList<>());
        when(requestService.getRequest(any(), any())).thenReturn(request);
        mvc.perform(get("/requests/{requestId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(request.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString()), LocalDateTime.class));
    }

    @Test
    void givenNotFoundExceptionInGetRequest_statusIsFound() throws Exception {
        when(requestService.getRequest(any(), any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/{requestId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequests(any())).thenReturn(Arrays.asList(requestDto));
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(requestDto))));
    }

}
package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody CreateRequestDto request) {
        return requestService.create(userId, request);
    }

    @GetMapping
    public Collection<RequestItemDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public Collection<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestItemDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {
        return requestService.getRequest(userId, requestId);
    }

}

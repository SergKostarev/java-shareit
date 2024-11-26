package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class RequestMapper {

    public static Request toRequest(CreateRequestDto requestDto, User user) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(user);
        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated());
    }

    public static RequestItemDto toRequestItemDto(Request request, List<Item> items) {
        return new RequestItemDto(request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated(),
                items.stream().map(ItemMapper::toItemRequestDto).toList()
        );
    }
}

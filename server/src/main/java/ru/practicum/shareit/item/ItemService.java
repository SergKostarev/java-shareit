package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto newItem);

    ItemCommentDto getById(Long userId, Long itemId);

    Collection<ItemCommentDto> getAllItems(Long userId);

    Collection<ItemDto> search(Long userId, String text);

    ItemDto delete(Long userId, Long id);

    CommentDto comment(Long userId, Long itemId, CreateCommentDto commentDto);

    Item getItemById(Long itemId);
}

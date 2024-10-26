package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, ItemUpdateDto newItem);

    ItemDto getById(Long userId, Long itemId);

    Collection<ItemDto> getAllItems(Long userId);

    Collection<ItemDto> search(Long userId, String text);
}

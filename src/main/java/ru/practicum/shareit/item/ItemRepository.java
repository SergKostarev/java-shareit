package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item newItem);

    Item getById(Long itemId);

    Collection<Item> getAllItems(Long userId);

    Collection<Item> search(String text);

    Item delete(Long id);
}

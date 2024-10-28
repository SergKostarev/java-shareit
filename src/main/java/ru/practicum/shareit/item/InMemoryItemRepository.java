package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long identifier = 0L;

    @Override
    public Item create(Item item) {
        identifier++;
        Long id = identifier;
        log.debug("Создание новой вещи с идентификатором " + id);
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        Item oldItem = getById(newItem.getId());
        if (!oldItem.getOwner().equals(newItem.getOwner())) {
            log.debug("Пользователь с идентификатором " + newItem.getOwner().getId() +
                    " не является владельцем вещи с идентификатором " + newItem.getId()
                    + ", обновление невозможно");
            throw new NotAuthorizedException("Недопустимая для пользователя операция",
                    newItem.getOwner().getId(), newItem.getId());
        }
        log.debug("Обновление вещи с идентификатором " + newItem.getId());
        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !oldItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    @Override
    public Item getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .map(item -> {
                    log.debug("Получение вещи с идентификатором " + itemId);
                    return item;
                })
                .orElseThrow(() -> {
                    log.debug("Вещь с идентификатором " + itemId + " не найдена.");
                    return new NotFoundException("Вещь не найдена.", itemId);
                });
    }

    @Override
    public Collection<Item> getAllItems(Long userId) {
        log.debug("Поиск вещей пользователя с идентификатором " + userId);
        return items
                .values()
                .stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Collection<Item> search(String text) {
        log.debug("Поиск вещей по тексту.");
        if (text.isBlank()) {
            return List.of();
        }
        return items
                .values()
                .stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase())
                    || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    @Override
    public Item delete(Long id) {
        Item item = getById(id);
        items.remove(id);
        return item;
    }
}
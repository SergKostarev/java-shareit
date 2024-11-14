package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional()
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.debug("Создание новой вещи");
        User owner = userRepository.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null); // TODO fix itemRequest
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional()
    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto newItemDto) {
        log.debug("Обновление вещи с идентификатором " + itemId);
        Item oldItem = itemRepository.getItemById(itemId);
        Item newItem = ItemMapper.toItem(newItemDto);
        if (!oldItem.getOwner().getId().equals(userId)) {
            log.debug("Пользователь с идентификатором " + userId +
                    " не является владельцем вещи с идентификатором " + oldItem.getId()
                    + ", обновление невозможно");
            throw new NotAuthorizedException("Недопустимая для пользователя операция",
                    userId, oldItem.getId());
        }
        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !oldItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemCommentDto getById(Long userId, Long itemId) {
        log.debug("Получение вещи с идентификатором " + itemId);
        userRepository.getUserById(userId);
        return ItemMapper.toItemDateDto(itemRepository.getItemById(itemId), List.of(),
                commentRepository.findAllByItemIdIn(Set.of(itemId)));
    }

    @Override
    public Collection<ItemCommentDto> getAllItems(Long userId) {
        log.debug("Поиск вещей пользователя с идентификатором " + userId);
        userRepository.getUserById(userId);
        Map<Long, Item> items = itemRepository
                .findByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Booking>> bookings = bookingRepository
                .findByItemIdIn(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> comments = commentRepository
                .findAllByItemIdIn(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        return items
                .values()
                .stream()
                .map(i -> ItemMapper.toItemDateDto(i, bookings.getOrDefault(i.getId(), List.of()),
                        comments.getOrDefault(i.getId(), List.of())))
                .toList();
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        log.debug("Поиск вещей по тексту.");
        if (text.isBlank()) {
            return List.of();
        }
        userRepository.getUserById(userId);
        return StreamSupport.stream(
                itemRepository.searchByAvailableAndNameOrDescription(text).spliterator(),
                        false)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional()
    @Override
    public ItemDto delete(Long userId, Long id) {
        userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(id);
        itemRepository.delete(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional()
    @Override
    public CommentDto comment(Long userId, Long itemId, CreateCommentDto commentDto) {
        User author = userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(itemId);
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            log.debug("Не найдены завершенные бронирования" +
                    " вещи с идентификатором " + itemId +
                    " для пользователя с идентификатором " + userId
                    + ", добавление комментария невозможно");
            throw new IncorrectDataException("Завершенные бронирования не найдены",
                    "идентификатор пользователя " + userId + ", идентификатор вещи " + itemId);
        }
        Comment comment = ItemMapper.toComment(commentDto, item, author);
        return ItemMapper.toCommentDto(commentRepository.save(comment));
    }
}

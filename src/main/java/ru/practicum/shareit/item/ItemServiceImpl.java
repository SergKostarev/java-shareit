package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.getById(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null); // TODO itemRequest
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto newItemDto) {
        User owner = userRepository.getById(userId);
        Item newItem = ItemMapper.toItem(itemId, newItemDto, owner, null); // TODO itemRequest
        return ItemMapper.toItemDto(itemRepository.update(newItem));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userRepository.getById(userId);
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        userRepository.getById(userId);
        return itemRepository
                .getAllItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        userRepository.getById(userId);
        return itemRepository
                .search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto delete(Long id) {
        Item item = itemRepository.delete(id);
        return ItemMapper.toItemDto(item);
    }
}

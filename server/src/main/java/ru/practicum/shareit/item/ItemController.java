package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody ItemUpdateDto newItemDto) {
        return itemService.update(userId, itemId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemCommentDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemCommentDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestParam("text") String text) {
        return itemService.search(userId, text);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long id) {
        return itemService.delete(userId, id);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto comment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestBody CreateCommentDto commentDto) {
        return itemService.comment(userId, itemId, commentDto);
    }

}

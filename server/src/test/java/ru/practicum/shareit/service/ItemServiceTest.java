package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    void createItem() {
        ItemDto itemDto = makeItemDto("Вещь",
                "Описание вещи", true, null);
        service.create(1L, itemDto);
        TypedQuery<Item> query = em.createQuery(
                "Select i from Item i where i.description = :description", Item.class);
        Item item = query.setParameter("description", itemDto.getDescription())
                .getSingleResult();
        User user = em.find(User.class, 1L);
        Assertions.assertThat(item)
                .isInstanceOf(Item.class)
                .hasNoNullFieldsOrPropertiesExcept("request")
                .hasFieldOrPropertyWithValue("name", "Вещь")
                .hasFieldOrPropertyWithValue("description", "Описание вещи")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("owner", user);
    }

    @Test
    void givenNonExistentUser_throwNotFoundException() {
        ItemDto itemDto = makeItemDto("Вещь",
                "Описание вещи", true, null);
        assertThatThrownBy(() -> {service.create(10L, itemDto);
            }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItem() {
        ItemUpdateDto itemDto = makeItemUpdateDto(1L,"Вещь",
                "Описание вещи", true, null);
        Assertions.assertThat(service.update(1L, 1L, itemDto))
                .isInstanceOf(ItemDto.class)
                .hasNoNullFieldsOrPropertiesExcept("requestId")
                .hasFieldOrPropertyWithValue("name", "Вещь")
                .hasFieldOrPropertyWithValue("description", "Описание вещи")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void givenNotOwnerUser_throwNotAuthorizedExceptionWhenUpdating() {
        ItemUpdateDto itemDto = makeItemUpdateDto(1L,"Вещь",
                "Описание вещи", true, null);
        assertThatThrownBy(() -> {service.update(2L, 1L, itemDto);
            }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    void getItemById() {
        List<CommentDto> comments = new ArrayList<>();
        Assertions.assertThat(service.getById(2L,3L))
                .isInstanceOf(ItemCommentDto.class)
                .hasNoNullFieldsOrPropertiesExcept("lastBooking", "nextBooking")
                .hasFieldOrPropertyWithValue("name", "Вещь по запросу")
                .hasFieldOrPropertyWithValue("description", "Описание вещи по запросу")
                .hasFieldOrPropertyWithValue("available", false)
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("comments", comments);
    }

    @Test
    void getAllItems() {
        Assertions.assertThat(service.getAllItems(2L)).hasSize(3);
    }

    @Test
    void givenPresentString_shouldFindItem() {
        Assertions.assertThat(
                service.search(2L, "Вещь, которую брали в АРЕНДУ")).hasSize(1);
    }

    @Test
    void givenNotPresentString_shouldNotFindItem() {
        Assertions.assertThat(
                service.search(2L, "Некая вещь").isEmpty());
    }

    @Test
    void givenPresentString_shouldNotFindUnavailableItem() {
        Assertions.assertThat(
                service.search(2L, "Описание вещи по запросу").isEmpty());
    }

    @Test
    void deleteItem() {
        Assertions.assertThat(service.delete(2L, 3L))
                .isInstanceOf(ItemDto.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("name", "Вещь по запросу")
                .hasFieldOrPropertyWithValue("description", "Описание вещи по запросу")
                .hasFieldOrPropertyWithValue("available", false)
                .hasFieldOrPropertyWithValue("requestId", 1L);
    }

    @Test
    void givenNotOwnerUser_throwNotAuthorizedExceptionWhenDeleting() {
        assertThatThrownBy(() -> {service.delete(1L, 3L);
            }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    void comment() {
        CommentDto commentDto = service.comment(
                1L, 4L, makeCreateCommentDto("Не очень"));
        Assertions.assertThat(commentDto)
                .isInstanceOf(CommentDto.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("text", "Не очень")
                .hasFieldOrPropertyWithValue("itemId", 4L)
                .hasFieldOrPropertyWithValue("authorName", "Петров");
    }

    @Test
    void givenUserWithNoBooking_throwNotAuthorizedExceptionWhenComment() {
        assertThatThrownBy(() -> {service.comment(
                2L, 3L, makeCreateCommentDto("Не очень"));
            }).isInstanceOf(IncorrectDataException.class);
    }

    private ItemDto makeItemDto(String name, String description, boolean available, Long requestId) {
        return new ItemDto(null, name, description, available, requestId);
    }

    private ItemUpdateDto makeItemUpdateDto(Long id, String name,
                                            String description, boolean available, Long requestId) {
        return new ItemUpdateDto(id, name, description, available, requestId);
    }

    private CreateCommentDto makeCreateCommentDto(String text) {
        return new CreateCommentDto(null, text);
    }

}

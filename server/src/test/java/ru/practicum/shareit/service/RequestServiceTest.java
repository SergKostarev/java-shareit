package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final EntityManager em;
    private final RequestService service;

    @Test
    void createRequest() {
        CreateRequestDto requestDto = makeCreateRequestDto("Запрос");
        service.create(1L, requestDto);
        TypedQuery<Request> query = em.createQuery(
                "Select r from Request r where r.description = :description", Request.class);
        Request request = query.setParameter("description", requestDto.getDescription())
                .getSingleResult();
        User user = em.find(User.class, 1L);
        Assertions.assertThat(request)
                .isInstanceOf(Request.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("description", "Запрос")
                .hasFieldOrPropertyWithValue("requestor", user);
    }

    @Test
    void getRequests() {
        Assertions.assertThat(service.getRequests(1L)).hasSize(1);
    }

    @Test
    void getAllRequests() {
        Assertions.assertThat(service.getAllRequests(1L)).isEmpty();
    }

    @Test
    void getRequest() {
        RequestItemDto requestItemDto = service.getRequest(1L, 1L);
        Assertions.assertThat(requestItemDto)
                .isInstanceOf(RequestItemDto.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("description", "Запрос на вещь")
                .hasFieldOrPropertyWithValue("requestorId", 1L);
        Assertions.assertThat(requestItemDto.getItems()).hasSize(1);
        Assertions.assertThat(requestItemDto.getItems().get(0))
                .isInstanceOf(ItemRequestDto.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("name", "Вещь по запросу")
                .hasFieldOrPropertyWithValue("ownerId", 2L);
    }

    private CreateRequestDto makeCreateRequestDto(String description) {
        return new CreateRequestDto(null, description);
    }
}

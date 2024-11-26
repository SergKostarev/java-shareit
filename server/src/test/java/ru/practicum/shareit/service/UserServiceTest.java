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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void createUser() {
        UserDto userDto = makeUserDto("Иванов", "some@email.com");
        service.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserById() {
        Assertions.assertThat(service.getById(1L))
                .isInstanceOf(UserDto.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Петров")
                .hasFieldOrPropertyWithValue("email", "some2@email.com");
    }

    @Test
    void getNonExistentUserById_throwNotFoundException() {
        assertThatThrownBy(() -> {service.getById(2L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser() {
        Assertions.assertThat(service.delete(1L))
                .isInstanceOf(UserDto.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Петров")
                .hasFieldOrPropertyWithValue("email", "some2@email.com");
    }

    @Test
    void updateUser() {
        UserUpdateDto userDto = makeUserUpdateDto("Иванов", "some@email.com");
        Assertions.assertThat(service.update(1L, userDto))
                .isInstanceOf(UserDto.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Иванов")
                .hasFieldOrPropertyWithValue("email", "some@email.com");
    }

    private UserDto makeUserDto(String name, String email) {
        return new UserDto(null, name, email);
    }

    private UserUpdateDto makeUserUpdateDto(String name, String email) {
        return new UserUpdateDto(name, email);
    }
}

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
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Assertions.assertThat(user)
                .isInstanceOf(User.class)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("name", "Иванов")
                .hasFieldOrPropertyWithValue("email", "some@email.com");
    }

    @Test
    void givenDuplicatedEmailUser_shouldNotCreateNewUser() {
        assertThatThrownBy(() -> {
            service.create(makeUserDto("Иванов", "sokolov@email.com"));
            }).isInstanceOf(DuplicatedDataException.class);
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
    void getNonExistentUserById_throwNotFoundExceptionWhenGetting() {
        assertThatThrownBy(() -> {
            service.getById(10L);
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

    @Test
    void givenDuplicatedEmailUser_shouldNotUpdateUser() {
        assertThatThrownBy(() -> {
            service.update(1L,
                makeUserUpdateDto("Иванов", "sidorov@email.com"));
            }).isInstanceOf(DuplicatedDataException.class);
    }

    private UserDto makeUserDto(String name, String email) {
        return new UserDto(null, name, email);
    }

    private UserUpdateDto makeUserUpdateDto(String name, String email) {
        return new UserUpdateDto(name, email);
    }
}

package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long identifier = 0L;

    @Override
    public User create(User user) {
        emailCheck(user.getEmail());
        identifier++;
        Long id = identifier;
        log.debug("Создание нового пользователя с идентификатором " + id);
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getById(newUser.getId());
        log.debug("Обновление пользователя с идентификатором " + newUser.getId());
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            emailCheck(newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }
        return oldUser;
    }

    @Override
    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .map(user -> {
                    log.debug("Получение пользователя с идентификатором " + id);
                    return user;
                })
                .orElseThrow(() -> {
                    log.debug("Пользователь с идентификатором " + id + " не найден.");
                    return new NotFoundException("Пользователь не найден.", id);
                });
    }

    @Override
    public User delete(Long id) {
        User user = getById(id);
        users.remove(id);
        return user;
    }

    private void emailCheck(String email) {
        Optional<User> user = users
                            .values()
                            .stream()
                            .filter(u -> u.getEmail().equals(email))
                            .findAny();
        if (user.isPresent()) {
            throw new DuplicatedDataException("Данный адрес электронной почты уже используется.",
                    user.get().getEmail());
        }
    }
}

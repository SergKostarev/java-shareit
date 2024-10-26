package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private static Long identifier = 0L;

    @Override
    public User create(User user) {
        Long id = identifier++;
        log.debug("Создание нового пользователя с идентификатором " + id);
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getById(newUser.getId());
        log.debug("Обновление пользователя с идентификатором " + newUser.getId());
        if (!newUser.getName().isEmpty() && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName()); // update name only?
        }
        return oldUser;
    }

    @Override
    public User getById(Long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        if (user.isPresent()) {
            log.debug("Получение пользователя с идентификатором " + id);
            return user.get();
        }
        log.debug("Пользователь с идентификатором " + id + " не найден.");
        throw new NotFoundException("Пользователь не найден.", id);
    }
}

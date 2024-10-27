package ru.practicum.shareit.user;

public interface UserRepository {
    User create(User user);

    User update(User newUser);

    User getById(Long id);

    User delete(Long id);
}

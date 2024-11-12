package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    default User getUserById(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден.", id));
    }

    Optional<User> findByEmail(String email);
}

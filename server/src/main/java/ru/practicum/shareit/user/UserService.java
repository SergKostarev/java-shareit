package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional()
    public UserDto create(UserDto userDto) {
        log.debug("Создание нового пользователя.");
        User user = UserMapper.toUser(userDto);
        Optional<User> existingEmailUser = userRepository.findByEmail(user.getEmail());
        if (existingEmailUser.isPresent()) {
            throw new DuplicatedDataException("Данный адрес электронной почты уже используется.",
                    existingEmailUser.get().getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional()
    public UserDto update(Long id, UserUpdateDto newUserDto) {
        User newUser = UserMapper.toUser(id, newUserDto);
        User oldUser = getUserById(newUser.getId());
        log.debug("Обновление пользователя с идентификатором " + newUser.getId());
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            Optional<User> existingEmailUser = userRepository.findByEmail(newUser.getEmail());
            if (existingEmailUser.isPresent()) {
                throw new DuplicatedDataException("Данный адрес электронной почты уже используется.",
                        existingEmailUser.get().getEmail());
            }
            oldUser.setEmail(newUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    public UserDto getById(Long id) {
        log.debug("Получение пользователя с идентификатором " + id);
        return UserMapper.toUserDto(getUserById(id));
    }

    @Transactional()
    public UserDto delete(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        return UserMapper.toUserDto(user);
    }

    public User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден.", userId));
    }
}

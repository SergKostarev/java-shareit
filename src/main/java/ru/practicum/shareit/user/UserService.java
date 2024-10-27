package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    public UserDto update(Long id, UserUpdateDto newUserDto) {
        User newUser = UserMapper.toUser(id, newUserDto);
        return UserMapper.toUserDto(userRepository.update(newUser));
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    public UserDto delete(Long id) {
        User user = userRepository.delete(id);
        return UserMapper.toUserDto(user);
    }
}

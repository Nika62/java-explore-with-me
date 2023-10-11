package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(Optional<Long[]> ids, int from, int size);

    void deleteUserById(@PathVariable long userId);
}

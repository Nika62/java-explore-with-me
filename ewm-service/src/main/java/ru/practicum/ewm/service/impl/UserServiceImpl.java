package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user;
        try {
            user = userRepository.save(userMapper.convertUserDtoToUser(userDto));
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
        }
        return userMapper.convertUserToUserDto(user);

    }

    @Override
    public List<UserDto> getUsers(Optional<Long[]> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids.isPresent()) {
            return userRepository.getUsersByIds(Arrays.asList(ids.get()), pageRequest).stream().map(userMapper::convertUserToUserDto).collect(Collectors.toList());
        }

        return userRepository.findAll(pageRequest).stream().map(userMapper::convertUserToUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("The required object was not found.", "User with id=" + userId + " was not found", LocalDateTime.now());
        }
        userRepository.deleteById(userId);
    }
}

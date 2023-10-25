package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;

@Component
public class UserMapper {
    public UserDto convertUserToUserDto(User user) {

        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public User convertUserDtoToUser(UserDto userDto) {

        if (userDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserShortDto convertUserToUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        UserShortDto userDto = new UserShortDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());

        return userDto;
    }
}

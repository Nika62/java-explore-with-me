package ru.practicum.ewm.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    @Email
    @NotBlank
    @Max(255)
    private String email;
    private long id;
    @NotBlank
    @Max(value = 255)
    private String name;
}

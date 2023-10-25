package ru.practicum.ewm.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private long id;
    @Email
    @Size(max = 254, min = 6)
    @NotBlank
    private String email;
    @Size(max = 250, min = 2)
    @NotBlank
    private String name;
}

package ru.practicum.ewm.model.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ObjectNotFoundException extends CustomParentException {
    public ObjectNotFoundException(String reason, String messege, LocalDateTime timestamp) {
        super(reason, messege, timestamp);
    }
}

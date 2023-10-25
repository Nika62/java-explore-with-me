package ru.practicum.ewm.model.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ObjectAlreadyExistsException extends CustomParentException {
    public ObjectAlreadyExistsException(String reason, String messege, LocalDateTime timestamp) {
        super(reason, messege, timestamp);
    }
}

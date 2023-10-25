package ru.practicum.ewm.model.exception;

import java.time.LocalDateTime;

public class ValidationException extends CustomParentException {
    public ValidationException(String reason, String messege, LocalDateTime timestamp) {
        super(reason, messege, timestamp);
    }
}

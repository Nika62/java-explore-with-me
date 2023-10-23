package ru.practicum.ewm.model.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomParentException extends RuntimeException {
    private String reason;
    private LocalDateTime timestamp;

    public CustomParentException(String reason, String message, LocalDateTime timestamp) {
        super(message);
        this.reason = reason;
        this.timestamp = timestamp;
    }
}

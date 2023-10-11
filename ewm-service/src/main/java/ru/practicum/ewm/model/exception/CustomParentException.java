package ru.practicum.ewm.model.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomParentException extends RuntimeException {
    private String reason;
    private LocalDateTime timestamp;

    public CustomParentException(String reason, String messege, LocalDateTime timestamp) {
        super(messege);
        this.reason = reason;
        this.timestamp = timestamp;
    }
}

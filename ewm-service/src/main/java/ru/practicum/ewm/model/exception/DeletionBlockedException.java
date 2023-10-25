package ru.practicum.ewm.model.exception;

import java.time.LocalDateTime;

public class DeletionBlockedException extends CustomParentException {
    public DeletionBlockedException(String reason, String messege, LocalDateTime timestamp) {
        super(reason, messege, timestamp);
    }
}

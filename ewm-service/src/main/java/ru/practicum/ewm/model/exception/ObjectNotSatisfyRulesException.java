package ru.practicum.ewm.model.exception;

import java.time.LocalDateTime;

public class ObjectNotSatisfyRulesException extends CustomParentException {

    public ObjectNotSatisfyRulesException(String reason, String messege, LocalDateTime timestamp) {
        super(reason, messege, timestamp);
    }
}

package ru.practicum.ewm.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.exception.CustomParentException;
import ru.practicum.ewm.model.exception.DeletionBlockedException;
import ru.practicum.ewm.model.exception.ObjectAlreadyExistsException;
import ru.practicum.ewm.model.exception.ObjectNotFoundException;
import ru.practicum.ewm.model.exception.ObjectNotSatisfyRulesException;
import ru.practicum.ewm.model.exception.ValidationException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ObjectAlreadyExistsException.class, DeletionBlockedException.class, ObjectNotSatisfyRulesException.class})
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleConflictException(final CustomParentException e) {
        return new ErrorResponse(CONFLICT.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundException(final ObjectNotFoundException e) {
        return new ErrorResponse(NOT_FOUND.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(BAD_REQUEST.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(BAD_REQUEST.name(), e.getObjectName(), e.getMessage(), convertToString(LocalDateTime.now()));
    }

    public class ErrorResponse {
        String status;
        String reason;
        String message;
        String timestamp;

        public ErrorResponse(String status, String reason, String message, String timestamp) {
            this.status = status;
            this.reason = reason;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}


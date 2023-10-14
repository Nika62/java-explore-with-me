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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ObjectAlreadyExistsException.class, DeletionBlockedException.class})
    @ResponseStatus(CONFLICT)
    ApiError handleConflictException(final CustomParentException e) {
        return new ApiError(String.valueOf(CONFLICT), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    ApiError handleNotFoundException(final ObjectNotFoundException e) {
        return new ApiError(String.valueOf(NOT_FOUND), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    ApiError handleEventDateTimeException(final ObjectNotSatisfyRulesException e) {
        return new ApiError(String.valueOf(BAD_REQUEST), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(I_AM_A_TEAPOT)
    ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(String.valueOf(I_AM_A_TEAPOT), e.getObjectName(), e.getMessage(), convertToString(LocalDateTime.now()));
    }

    public class ApiError {
        private List<String> errors;
        private String massege;
        private String reason;
        private String status;
        private String timestamp;

        public ApiError(String status, String reason, String massege, String timestamp) {
            this.status = status;
            this.reason = reason;
            this.massege = massege;
            this.timestamp = timestamp;
        }

        public ApiError(List<String> errors, String massege, String reason, String status, String timestamp) {
            this.errors = errors;
            this.massege = massege;
            this.reason = reason;
            this.status = status;
            this.timestamp = timestamp;
        }
    }
}


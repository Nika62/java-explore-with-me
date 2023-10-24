package ru.practicum.ewm.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.model.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ObjectAlreadyExistsException.class, DeletionBlockedException.class, ObjectNotSatisfyRulesException.class})
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleConflictException(final CustomParentException e) {
        return new ErrorResponse(getErrors(e), CONFLICT.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundException(final ObjectNotFoundException e) {
        return new ErrorResponse(getErrors(e), NOT_FOUND.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(getErrors(e), BAD_REQUEST.name(), e.getReason(), e.getMessage(), convertToString(e.getTimestamp()));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(getErrors(e), BAD_REQUEST.name(), e.getObjectName(), e.getMessage(), convertToString(LocalDateTime.now()));
    }

    public class ErrorResponse {
        List<String> errors;
        String status;
        String reason;
        String message;
        String timestamp;

        public ErrorResponse(List<String> errors, String status, String reason, String message, String timestamp) {
            this.errors = errors;
            this.status = status;
            this.reason = reason;
            this.message = message;
            this.timestamp = timestamp;
        }

        public List<String> getErrors() {
            return errors;
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

    public List<String> getErrors(Exception e) {
        List<String> errors = new ArrayList<>();
        StackTraceElement[] stackTrace = e.getStackTrace();
        int size = stackTrace.length <= 5 ? 5 : stackTrace.length;
        for (int i = 0; i <= size; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            StringBuilder error = new StringBuilder();
            error.append("File: " + stackTraceElement.getFileName() + ", ");
            error.append("Method: " + stackTraceElement.getMethodName() + ", ");
            error.append("Line number: " + stackTraceElement.getLineNumber() + ".");
            errors.add(error.toString());
        }
        return errors;
    }
}


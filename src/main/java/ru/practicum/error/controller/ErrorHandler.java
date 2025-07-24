package ru.practicum.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exception.DuplicateAggregateException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.IllegalArgumentException;
import ru.practicum.error.exception.ResponseStatusException;
import ru.practicum.error.model.ApiError;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFound(EntityNotFoundException e) {
        log.warn(e.getMessage(), e);
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Entity not found")
                .status(HttpStatus.NOT_FOUND.name())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException e) {
        log.warn(e.getMessage(), e);
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Invalid request parameters")
                .status(HttpStatus.BAD_REQUEST.name())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ApiError handleResponseStatusException(ResponseStatusException e) {
        log.warn(e.getMessage(), e);

        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("ResponseStatusException")
                .status(HttpStatus.BAD_REQUEST.name())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DuplicateAggregateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicateAggregateException(DuplicateAggregateException e) {
        log.warn(e.getMessage(), e);

        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message("Unable to create aggregate due to conflict")
                .reason("Duplicate resource")
                .status(HttpStatus.CONFLICT.name())
                .localDateTime(LocalDateTime.now())
                .build();

    }
}

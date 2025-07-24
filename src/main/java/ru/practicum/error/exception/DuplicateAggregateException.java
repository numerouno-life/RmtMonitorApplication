package ru.practicum.error.exception;

public class DuplicateAggregateException extends RuntimeException {
    public DuplicateAggregateException(String message) {
        super(message);
    }
}

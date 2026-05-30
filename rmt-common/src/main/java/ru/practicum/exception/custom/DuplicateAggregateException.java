package ru.practicum.exception.custom;

public class DuplicateAggregateException extends RuntimeException {
    public DuplicateAggregateException(String message) {
        super(message);
    }
}

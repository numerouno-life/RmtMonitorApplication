package ru.practicum.exception.custom;

public class ResponseStatusException extends RuntimeException {
    public ResponseStatusException(String message) {
        super(message);
    }
}

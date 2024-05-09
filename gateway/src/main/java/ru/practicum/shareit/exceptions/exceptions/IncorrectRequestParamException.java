package ru.practicum.shareit.exceptions.exceptions;

public class IncorrectRequestParamException extends RuntimeException {
    public IncorrectRequestParamException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exceptions.exceptions;

public class IncorrectBookingTimeException extends RuntimeException {
    public IncorrectBookingTimeException(String message) {
        super(message);
    }
}

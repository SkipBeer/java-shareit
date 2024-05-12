package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.exceptions.IncorrectBookingTimeException;
import ru.practicum.shareit.exceptions.exceptions.IncorrectRequestParamException;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({IncorrectRequestParamException.class, IncorrectBookingTimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final RuntimeException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ErrorResponse;

public class ExceptionTest {

    @Test
    void errorResponseTest() {
        String currentError = "error";
        ErrorResponse response = new ErrorResponse(currentError);
        String error =  response.getError();
        Assertions.assertNotEquals(response, null);
        Assertions.assertEquals(currentError, error);
    }
}

package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BookingMapperTest {

    @Test
    void bookingMapperConstructorTest() {
        BookingMapper mapper = new BookingMapper();

        Assertions.assertNotEquals(mapper, null);
    }
}

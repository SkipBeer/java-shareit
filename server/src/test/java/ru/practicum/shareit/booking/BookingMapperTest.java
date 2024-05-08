package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import java.time.LocalDateTime;

public class BookingMapperTest {

    @Test
    void bookingMapperConstructorTest() {
        BookingCreationDto creationDto = new BookingCreationDto(
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());
        BookingMapper mapper = new BookingMapper();

        Booking booking = BookingMapper.fromBookingCreationDto(creationDto);

        Assertions.assertNotEquals(mapper, null);
        Assertions.assertEquals(creationDto.getStart(), booking.getStart());
        Assertions.assertEquals(creationDto.getEnd(), booking.getEnd());
    }
}

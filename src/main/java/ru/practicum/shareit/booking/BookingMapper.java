package ru.practicum.shareit.booking;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static Booking fromBookingCreationDto(BookingCreationDto dto) {
        return new Booking(null,
                dto.getStart(),
                dto.getEnd(),
                null,
                null,
                BookingStatus.WAITING.name());
    }

    public static Booking fromBookingDto(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getBooker().getId());
    }
}

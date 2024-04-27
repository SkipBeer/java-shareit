package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingCreationDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return bookingService.add(bookingDto, sharerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long sharerId,
                                    @RequestParam(name = "approved") String approveStatus) {
        return bookingService.update(bookingId, sharerId, approveStatus);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return bookingService.getById(bookingId, sharerId);
    }

    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getAllByState(sharerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size) {
        return bookingService.getAllByOwnerAndState(sharerId, state, from, size);
    }


}

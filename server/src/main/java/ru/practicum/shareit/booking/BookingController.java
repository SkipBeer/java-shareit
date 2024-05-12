package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingCreationDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long sharerId,
                                    @RequestParam(name = "approved") String approveStatus) {
        log.info("Update booking id={} approve={} by user id={}", bookingId, approveStatus, sharerId);
        return bookingService.update(bookingId, sharerId, approveStatus);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("Get booking {}, userId={}", bookingId, sharerId);
        return bookingService.getById(bookingId, sharerId);
    }

    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size,
                                           @RequestParam(name = "time", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        log.info("Get bookings with state {}, userId={}, from={}, size={}, time={}", state, size, from, size, time);
        return bookingService.getAllByState(sharerId, state, from, size, time);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size,
                                           @RequestParam(name = "time", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        log.info("Getting all bookings for owner id={} by params state={} from={} size={}, time={}",
                sharerId, state, from, size, time);
        return bookingService.getAllByOwnerAndState(sharerId, state, from, size, time);
    }


}

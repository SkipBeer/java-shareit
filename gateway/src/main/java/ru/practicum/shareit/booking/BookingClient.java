package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.exceptions.IncorrectBookingTimeException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        LocalDateTime time = LocalDateTime.now();
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size,
                "time", time
        );
        return get("?state={state}&from={from}&size={size}&time={time}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        validate(requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> update(long bookingId, long userId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, BookingState state, Integer from, Integer size) {
        LocalDateTime time = LocalDateTime.now();
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size,
                "time", time
        );
        return get("/owner?state={state}&from={from}&size={size}&time={time}", userId, parameters);
    }

    private void validate(BookItemRequestDto booking) {
        if (booking.getEnd() == null || booking.getStart() == null) {
            throw new IncorrectBookingTimeException("Необходимо указать время бронирования");
        }

        if (booking.getStart().equals(booking.getEnd())) {
            throw new IncorrectBookingTimeException("Начало и конец бронирования не могут совпадать");
        }

        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectBookingTimeException("Некорректно указано время бронирования");
        }

        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new IncorrectBookingTimeException("Конец бронирования не может быть раньше начала");
        }

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new IncorrectBookingTimeException("Начало бронирования не может быть позже конца");
        }
    }
}
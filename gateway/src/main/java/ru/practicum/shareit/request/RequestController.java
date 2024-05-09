package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                     @RequestBody RequestCreationDto dto) {
        log.info("Add request {} by user id={}", dto, sharerId);
        return requestClient.add(dto, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsForUser(@RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("Get request for user id={}", sharerId);
        return requestClient.getForUser(sharerId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long sharerId, @PathVariable long requestId) {
        log.info("Get request id={} by user id={}", requestId, sharerId);
        return requestClient.getRequest(sharerId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsFromOtherUsers(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get request from all users by user is={}, params from={}, size={}", sharerId, from, size);
        return requestClient.getAllFromOtherUsers(sharerId, from, size);

    }
}
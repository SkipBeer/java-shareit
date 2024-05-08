package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                     @RequestBody ItemRequestCreationDto dto) {
        log.info("Add request {} by user id={}", dto, sharerId);
        return requestService.add(dto, sharerId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsForUser(@RequestHeader("X-Sharer-User-Id") Long sharerId) {
        log.info("Get request for user id={}", sharerId);
        return requestService.getForUser(sharerId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long sharerId, @PathVariable Long requestId) {
        log.info("Get request id={} by user id={}", requestId, sharerId);
        return requestService.getRequest(sharerId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsFromOtherUsers(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                                          @RequestParam(name = "from", required = false) Integer from,
                                                          @RequestParam(name = "size", required = false) Integer size) {
        log.info("Get request from all users by user is={}, params from={}, size={}", sharerId, from, size);
        return requestService.getAllFromOtherUsers(sharerId, from, size);

    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("Creating item {}", item);
        return itemService.add(item, sharerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") long sharerId, @RequestBody ItemDto patch) {
        log.info("Update item with id={} by user id={} patch={}", itemId, sharerId, patch);
        return itemService.update(patch, itemId, sharerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long sharerId,
                               @RequestParam(name = "time", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        log.info("Getting item with id={} by user id={}, time={}", itemId, sharerId, time);
        return itemService.getById(itemId, sharerId, time);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Long sharerId,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        log.info("Getting all items for user id={}, time={}", sharerId, time);
        return itemService.getItemsByUserId(sharerId, time);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                @RequestParam(name = "text") String searchText,
                                @RequestParam(name = "from", required = false) Integer from,
                                @RequestParam(name = "size", required = false) Integer size) {
        log.info("Searching items by text={}", searchText);
        return itemService.search(searchText);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentCreationDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long sharerId, @PathVariable Long itemId) {
        log.info("Add comment {}", commentDto);
        return itemService.addComment(commentDto, sharerId, itemId);
    }

}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemCreationDto item,
                                          @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("Creating item {}", item);
        return itemClient.add(item, sharerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long sharerId, @RequestBody ItemCreationDto patch) {
        log.info("Update item with id={} by user id={} patch={}", itemId, sharerId, patch);
        return itemClient.update(patch, itemId, sharerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("Getting item with id={} by user id={}", itemId, sharerId);
        return itemClient.getItem(itemId, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size) {
        log.info("Getting all items for user id={}", sharerId);
        return itemClient.getItemsByUserId(sharerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                         @RequestParam(name = "text") String searchText,
                                @RequestParam(name = "from", required = false) Integer from,
                                @RequestParam(name = "size", required = false) Integer size) {
        log.info("Searching items by text={}", searchText);
        return itemClient.search(sharerId, searchText);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentCreationDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") long sharerId, @PathVariable long itemId) {
        log.info("Add comment {}", commentDto);
        return itemClient.addComment(commentDto, sharerId, itemId);
    }

}

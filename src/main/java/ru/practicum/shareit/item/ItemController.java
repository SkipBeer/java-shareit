package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") String sharerId) {
        return itemService.add(item, sharerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") String sharerId, @RequestBody ItemDto patch) {
        return itemService.update(patch, itemId, sharerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.getById(itemId, sharerId);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Long sharerId) {
        return itemService.getItemsByUserId(sharerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchText) {
        return itemService.search(searchText);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentCreationDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long sharerId, @PathVariable Long itemId) {
        return itemService.addComment(commentDto, sharerId, itemId);
    }

}

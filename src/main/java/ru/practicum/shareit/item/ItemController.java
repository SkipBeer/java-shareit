package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") String sharerId) {
        return itemService.add(item, sharerId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") String sharerId, @RequestBody Item patch) {
        return itemService.update(patch, itemId, sharerId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") String sharerId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<Item> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") String sharerId) {
        return itemService.getItemsByUserId(sharerId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(name = "text") String searchText) {
        return itemService.search(searchText);
    }

}

package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exceptions.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class InMemoryItemRepository {

    Map<Long, Item> items = new HashMap<>();

    private long generatorId = 1;

    private long generateId() {
        return generatorId++;
    }


    public Item add(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }


    public Item getById(Long id) {
        Item item = items.get(id);

        if (item == null) {
            throw new ItemNotFoundException("Товар с указанным id не найден");
        }
        return item;
    }


    public Item update(Long id, Item item) {
        items.put(id, item);
        return getById(id);
    }


    public List<Item> getByUserId(Long userId) {
        return items.values().stream().filter(item -> item.getUserId().equals(userId)).collect(Collectors.toList());
    }


    public void delete(Long id) {
        items.remove(id);
    }

    public List<Item> search(String searchText) {
        if (searchText.equals("") || searchText.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText)) && item.getAvailable()).collect(Collectors.toList());
    }
}

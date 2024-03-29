package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item add(Item item);

    Item getById(Long id);

    Item update(Long id, Item item);

    void delete(Long id);

    List<Item> getByUserId(Long userId);

    List<Item> search(String searchText);
}

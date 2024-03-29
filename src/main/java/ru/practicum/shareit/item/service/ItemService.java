package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.exceptions.MissingRequiredFieldsException;
import ru.practicum.shareit.exceptions.exceptions.NoRightsException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;

    public Item add(Item item, String sharerId) {
        item.setOwner(Long.parseLong(sharerId));
        validate(item);
        return itemRepository.add(item);
    }

    public Item update(Item patch, Long itemId, String sharerId) {
        Item existsItem = itemRepository.getById(itemId);
        if (existsItem.getOwner() != Long.parseLong(sharerId)) {
            throw new NoRightsException("У пользователя с id=" + sharerId + " нет прав для редактирования этого товара");
        }
        Item updatedUser = customApplyPatchToItem(patch,
                new Item(itemId, existsItem.getName(), existsItem.getDescription(),
                        existsItem.getAvailable(), existsItem.getOwner(), existsItem.getRequest()));
        return itemRepository.update(itemId, updatedUser);
    }

    public Item getById(Long itemId) {
        return itemRepository.getById(itemId);
    }

    public List<Item> getItemsByUserId(String sharerId) {
        return itemRepository.getByUserId(Long.parseLong(sharerId));
    }

    public List<Item> search(String searchText) {
        return itemRepository.search(searchText.toLowerCase());
    }


    private void validate(Item item) {
        if (userRepository.getById(item.getOwner()) == null) {
            throw new UserNotFoundException("Владелец с id " + item.getOwner() + " не найден");
        }

        if (item.getAvailable() == null || item.getName().isEmpty() || item.getDescription() == null) {
            throw new MissingRequiredFieldsException("Поля available, name и description не могут быть пустыми");
        }
    }

    private Item customApplyPatchToItem(Item patch, Item targetItem) {
        if (patch.getName() != null && !patch.getName().isEmpty()) {
            targetItem.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            targetItem.setDescription(patch.getDescription());
        }
        if (!targetItem.getAvailable().equals(patch.getAvailable()) && patch.getAvailable() != null) {
            targetItem.setAvailable(patch.getAvailable());
        }
        return targetItem;
    }


}

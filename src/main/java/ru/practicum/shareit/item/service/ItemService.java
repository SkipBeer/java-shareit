package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.exceptions.MissingRequiredFieldsException;
import ru.practicum.shareit.exceptions.exceptions.NoRightsException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto add(ItemDto item, String sharerId) {
        item.setOwner(Long.parseLong(sharerId));
        validate(item);
        return ItemMapper.toItemDto(itemRepository.add(ItemMapper.fromItemDto(item)));
    }

    public ItemDto update(ItemDto patch, Long itemId, String sharerId) {
        Item existsItem = itemRepository.getById(itemId);
        if (existsItem.getOwner() != Long.parseLong(sharerId)) {
            throw new NoRightsException("У пользователя с id=" + sharerId + " нет прав для редактирования этого товара");
        }
        customApplyPatchToItem(patch, existsItem);
        return ItemMapper.toItemDto(itemRepository.update(itemId, existsItem));
    }

    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    public List<ItemDto> getItemsByUserId(String sharerId) {
        return itemRepository.getByUserId(Long.parseLong(sharerId))
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> search(String searchText) {
        return itemRepository.search(searchText.toLowerCase())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }


    private void validate(ItemDto item) {
        if (userRepository.getById(item.getOwner()) == null) {
            throw new UserNotFoundException("Владелец с id " + item.getOwner() + " не найден");
        }

        if (item.getAvailable() == null || item.getName().isEmpty() || item.getDescription() == null) {
            throw new MissingRequiredFieldsException("Поля available, name и description не могут быть пустыми");
        }
    }

    private void customApplyPatchToItem(ItemDto patch, Item targetItem) {
        if (patch.getName() != null && !patch.getName().isEmpty()) {
            targetItem.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            targetItem.setDescription(patch.getDescription());
        }
        if (!targetItem.getAvailable().equals(patch.getAvailable()) && patch.getAvailable() != null) {
            targetItem.setAvailable(patch.getAvailable());
        }
    }


}

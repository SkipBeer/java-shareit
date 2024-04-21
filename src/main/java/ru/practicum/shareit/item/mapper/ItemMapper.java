package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getUser().getId(),
                item.getRequest(),
                null,
                null,
                null
        );
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                itemDto.getRequest()
        );
    }

}

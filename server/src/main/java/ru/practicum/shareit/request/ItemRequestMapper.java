package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), null);
    }

    public static ItemRequest fromCreationDto(ItemRequestCreationDto dto) {
        return new ItemRequest(null, dto.getDescription(), null, LocalDateTime.now());
    }
}

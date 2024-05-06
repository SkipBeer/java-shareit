package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

public class RequestMapperTest {

    @Test
    void toDtoTest() {
        ItemRequest request = new ItemRequest(1L, "a", null, LocalDateTime.now());

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        Assertions.assertEquals(request.getId(), dto.getId());
        Assertions.assertEquals(request.getDescription(), dto.getDescription());
        Assertions.assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void fromCreationDtoTest() {
        ItemRequestCreationDto creationDto = new ItemRequestCreationDto("a");

        ItemRequest request = ItemRequestMapper.fromCreationDto(creationDto);

        Assertions.assertEquals(creationDto.getDescription(), request.getDescription());

    }

    @Test
    void mapperConstructorTest() {
        ItemRequestMapper mapper = new ItemRequestMapper();

        Assertions.assertNotEquals(null, mapper);
    }
}

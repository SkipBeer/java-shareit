package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemControllerTest {

    private ItemService service = Mockito.mock(ItemService.class);

    private ItemController controller = new ItemController(service);

    private ItemDto dto;


    @BeforeEach
    private void setUp() {
        dto = new ItemDto(1L,
                "a",
                "b",
                true,
                null,
                null,
                null,
                null,
                null);
    }

    @Test
    void addItemTest() {
       Mockito.when(service.add(Mockito.any(), Mockito.anyString())).thenReturn(dto);

       ItemDto newItem = controller.addItem(dto, "1L");

        Assertions.assertEquals(newItem, dto);
    }

    @Test
    void updateTest() {
        Mockito.when(service.update(Mockito.any(), Mockito.anyLong(), Mockito.any())).thenReturn(dto);

        ItemDto itemDto = controller.update(1L, "1", dto);

        Assertions.assertEquals(itemDto, dto);
    }

    @Test
    void getItemByIdTest() {
        Mockito.when(service.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(dto);

        ItemDto existsItem = controller.getItemById(1L, 1L);

        Assertions.assertEquals(existsItem, dto);
    }

    @Test
    void getAllUsersItemsTest() {
        List<ItemDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        Mockito.when(service.getItemsByUserId(Mockito.anyLong())).thenReturn(dtoList);

        List<ItemDto> newDtoList = controller.getAllUsersItems(1L, null, null);

        Assertions.assertEquals(dtoList.get(0), newDtoList.get(0));
    }

    @Test
    void searchTest() {
        List<ItemDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        Mockito.when(service.search(Mockito.any())).thenReturn(dtoList);

        List<ItemDto> newDtoList = controller.search("a", null, null);

        Assertions.assertEquals(dtoList.get(0), newDtoList.get(0));
    }

    @Test
    void addCommentTest() {
        CommentCreationDto creationDto = new CommentCreationDto("text");
        CommentDto commentDto = new CommentDto(1L, creationDto.getText(), "b", LocalDateTime.now());

        Mockito.when(service.addComment(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(commentDto);

        CommentDto newDto = controller.addComment(creationDto, 1L, 1L);

        Assertions.assertEquals(newDto, commentDto);

    }

}

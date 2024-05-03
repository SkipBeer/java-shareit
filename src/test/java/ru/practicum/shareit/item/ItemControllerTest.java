package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService service;

    @InjectMocks
    private ItemController controller;

    private ItemDto dto;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        dto = new ItemDto(1L,
                "a",
                "b",
                true,
                null,
                null,
                null,
                null,
                null);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addItemTest() throws Exception {
       Mockito.when(service.add(Mockito.any(), Mockito.anyString())).thenReturn(dto);

       ItemDto newItem = controller.addItem(dto, "1L");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        Assertions.assertEquals(newItem, dto);
    }

    @Test
    void updateTest() throws Exception {
        Mockito.when(service.update(Mockito.any(), Mockito.anyLong(), Mockito.any())).thenReturn(dto);

        ItemDto itemDto = controller.update(1L, "1", dto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        Assertions.assertEquals(itemDto, dto);
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(service.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(dto);

        ItemDto existsItem = controller.getItemById(1L, 1L);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        Assertions.assertEquals(existsItem, dto);
    }

    @Test
    void getAllUsersItemsTest() throws Exception {
        List<ItemDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        Mockito.when(service.getItemsByUserId(Mockito.anyLong())).thenReturn(dtoList);

        List<ItemDto> newDtoList = controller.getAllUsersItems(1L, null, null);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Отвертка")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(dtoList.get(0), newDtoList.get(0));
    }

    @Test
    void searchTest() throws Exception {
        List<ItemDto> dtoList = new ArrayList<>();
        dtoList.add(dto);

        Mockito.when(service.search(Mockito.any())).thenReturn(dtoList);

        List<ItemDto> newDtoList = controller.search("a", null, null);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(dtoList.get(0), newDtoList.get(0));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentCreationDto creationDto = new CommentCreationDto("text");
        CommentDto commentDto = new CommentDto(1L, creationDto.getText(), "b", LocalDateTime.now());

        Mockito.when(service.addComment(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(commentDto);

        CommentDto newDto = controller.addComment(creationDto, 1L, 1L);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        Assertions.assertEquals(newDto, commentDto);

    }

}

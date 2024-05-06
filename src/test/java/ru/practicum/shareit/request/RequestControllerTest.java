package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;

    private ItemRequestCreationDto creationDto;

    private ItemRequestDto dto;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        creationDto = new ItemRequestCreationDto("A");
        dto = new ItemRequestDto(1L, creationDto.getDescription(),
                LocalDateTime.now(), null);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    void addRequestTest() throws Exception {
        Mockito.when(service.add(Mockito.any(), Mockito.anyLong())).thenReturn(dto);

        ItemRequestDto newDto = controller.addRequest(1L, creationDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(creationDto.getDescription())));

        Assertions.assertEquals(newDto, dto);
    }

    @Test
    void getRequestsForUserTest() throws Exception {
        Mockito.when(service.getForUser(Mockito.anyLong())).thenReturn(new ArrayList<>());

        List<ItemRequestDto> dtoList = controller.getRequestsForUser(1L);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(dtoList, new ArrayList<>());
    }

    @Test
    void getRequestTest() throws Exception {
        Mockito.when(service.getRequest(Mockito.anyLong(), Mockito.anyLong())).thenReturn(dto);

        ItemRequestDto newDto = controller.getRequest(1L, 1L);

        mvc.perform(get("/requests/{id}", dto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        Assertions.assertEquals(dto, newDto);
    }

    @Test
    void getRequestFromOtherUsersTest() throws Exception {
        controller.getRequestsFromOtherUsers(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(service, Mockito.times(1))
                .getAllFromOtherUsers(Mockito.anyLong(), Mockito.any(), Mockito.any());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

    private BookingCreationDto creationDto;

    private BookingDto dto;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private final LocalDateTime start = LocalDateTime.now().minusSeconds(1);
    private final LocalDateTime end = LocalDateTime.now().plusSeconds(1);

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        creationDto = new BookingCreationDto(
                1L,
                1L,
                1L,
                null,
                null);
        dto = new BookingDto(1L, start, end, null, null, null);
    }

    @Test
    void addTest() throws Exception {
        controller.addBooking(creationDto, 1L);

        Mockito.verify(service, Mockito.times(1)).add(Mockito.any(), Mockito.anyLong());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateTest() throws Exception {
        controller.updateBooking(1L, 1L, BookingStatus.APPROVED.name());

        Mockito.verify(service, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());

        mvc.perform(patch("/bookings/{bookingId}", creationDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        controller.getBookingById(1L, 1L);
        Mockito.when(service.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(dto);
        Mockito.verify(service, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());

        mvc.perform(get("/bookings/{bookingId}", dto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByUserId() throws Exception {
        controller.getAllByUserId(1L, "state", 0, 20);

        Mockito.verify(service, Mockito.times(1))
                .getAllByState(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllForOwner() throws Exception {
        controller.getAllForOwner(1L, "state",0, 20);

        Mockito.verify(service, Mockito.times(1))
                .getAllByOwnerAndState(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(creationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

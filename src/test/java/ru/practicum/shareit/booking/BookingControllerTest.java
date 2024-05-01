package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import java.time.LocalDateTime;

public class BookingControllerTest {

    private final BookingService service = Mockito.mock(BookingService.class);

    private final BookingController controller = new BookingController(service);

    private BookingCreationDto creationDto = new BookingCreationDto(1L,
            1L,
            1L,
            LocalDateTime.now().minusSeconds(1),
            LocalDateTime.now().plusSeconds(1));

    @Test
    void addTest() {
        controller.addBooking(creationDto, 1L);

        Mockito.verify(service, Mockito.times(1)).add(Mockito.any(), Mockito.anyLong());
    }

    @Test
    void updateTest() {
        controller.updateBooking(1L, 1L, BookingStatus.APPROVED.name());

        Mockito.verify(service, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getBookingByIdTest() {
        controller.getBookingById(1L, 1L);

        Mockito.verify(service, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getAllByUserId() {
        controller.getAllByUserId(1L, "state", 0, 20);

        Mockito.verify(service, Mockito.times(1))
                .getAllByState(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getAllForOwner() {
        controller.getAllForOwner(1L, "state",0, 20);

        Mockito.verify(service, Mockito.times(1))
                .getAllByOwnerAndState(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }
}

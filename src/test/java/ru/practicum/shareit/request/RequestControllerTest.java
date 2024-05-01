package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestControllerTest {

    private ItemRequestService service = Mockito.mock(ItemRequestService.class);

    private ItemRequestController controller = new ItemRequestController(service);

    private ItemRequestCreationDto creationDto = new ItemRequestCreationDto("A");

    private ItemRequestDto dto = new ItemRequestDto(1L, creationDto.getDescription(),
            LocalDateTime.now(), null);

    @Test
    void addRequestTest() {
        Mockito.when(service.add(Mockito.any(), Mockito.anyLong())).thenReturn(dto);

        ItemRequestDto newDto = controller.addRequest(1L, creationDto);

        Assertions.assertEquals(newDto, dto);
    }

    @Test
    void getRequestsForUserTest() {
        Mockito.when(service.getForUser(Mockito.anyLong())).thenReturn(new ArrayList<>());

        List<ItemRequestDto> dtoList = controller.getRequestsForUser(1L);

        Assertions.assertEquals(dtoList, new ArrayList<>());
    }

    @Test
    void getRequestTest() {
        Mockito.when(service.getRequest(Mockito.anyLong(), Mockito.anyLong())).thenReturn(dto);

        ItemRequestDto newDto = controller.getRequest(1L, 1L);

        Assertions.assertEquals(dto, newDto);
    }

    @Test
    void getRequestFromOtherUsersTest() {
        controller.getRequestsFromOtherUsers(Mockito.anyLong(), Mockito.anyInt() , Mockito.anyInt());

        Mockito.verify(service, Mockito.times(1))
                .getAllFromOtherUsers(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }
}

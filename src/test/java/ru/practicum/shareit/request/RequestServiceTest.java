package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.exceptions.IncorrectRequestParamException;
import ru.practicum.shareit.exceptions.exceptions.MissingRequiredFieldsException;
import ru.practicum.shareit.exceptions.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestServiceTest {

    private ItemRequestRepository requestRepository = Mockito.mock(ItemRequestRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private ItemRequestService service = new ItemRequestService(requestRepository, userRepository, itemRepository);

    private ItemRequestDto dto;
    private ItemRequestCreationDto creationDto;

    private ItemRequest request;
    private User user;
    private Item item;


    @BeforeEach
    void setUp() {
        dto = new ItemRequestDto(1L, "a", LocalDateTime.now(), new ArrayList<>());
        creationDto = new ItemRequestCreationDto("a");
        request = new ItemRequest(1L, "a", null, LocalDateTime.now());
        user = new User(1L, "a", "b@ya.ru");
        item = new Item(1L, "a", "b", true, user, request);
    }

    @Test
    void addRequestTest() {
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(request);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));

        ItemRequestDto savedRequest = service.add(creationDto, 1L);
        savedRequest.setItems(new ArrayList<>());
        Assertions.assertEquals(savedRequest.getId(), dto.getId());
    }

    @Test
    void addRequestBlankDescriptionTest() {
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(request);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        creationDto.setDescription("");


        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(creationDto, 1L));

        Assertions.assertEquals(exception.getMessage(), "Описание запроса не может быть пустым");
    }

    @Test
    void addRequestNullDescriptionTest() {
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(request);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));

        creationDto.setDescription(null);
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(creationDto, 1L));

        Assertions.assertEquals(exception.getMessage(), "Описание запроса не может быть пустым");
    }


    @Test
    void addRequestWrongUserTest() {
        Mockito.when(requestRepository.save(Mockito.any())).thenReturn(request);

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.add(creationDto, 1L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void getForUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));

        List<Item> items = new ArrayList<>();
        items.add(item);
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(items);
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(request);

        Mockito.when(requestRepository.findByRequestorId(Mockito.anyLong())).thenReturn(requestList);

        List<ItemRequestDto> dtoList = service.getForUser(1L);

        Assertions.assertEquals(dtoList.get(0).getId(), dto.getId());
    }

    @Test
    void getForWrongUserTest() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(items);
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(request);

        Mockito.when(requestRepository.findByRequestorId(Mockito.anyLong())).thenReturn(requestList);

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getForUser(1L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void getForUserEmptyItemsTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(new ArrayList<>());
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(request);

        Mockito.when(requestRepository.findByRequestorId(Mockito.anyLong())).thenReturn(requestList);

        List<ItemRequestDto> dtoList = service.getForUser(1L);

        Assertions.assertEquals(dtoList.get(0).getId(), dto.getId());
    }

    @Test
    void getAllFromOtherUsersNullPaginationTest() {
        List<ItemRequestDto> dtoList = service.getAllFromOtherUsers(1L, null, null);
        Assertions.assertEquals(new ArrayList<ItemRequestDto>(), dtoList);
    }

    @Test
    void getAllFromOtherUsersNegativePaginationTest() {
        final IncorrectRequestParamException exception =  Assertions.assertThrows(
                IncorrectRequestParamException.class, () -> service.getAllFromOtherUsers(1L, -1, -1));
        Assertions.assertEquals(exception.getMessage(), "Некорректные параметры постраничного отображения");

    }

    @Test
    void getAllFromOtherUsersZeroPaginationTest() {
        final IncorrectRequestParamException exception =  Assertions.assertThrows(
                IncorrectRequestParamException.class, () -> service.getAllFromOtherUsers(1L, 0, 0));
        Assertions.assertEquals(exception.getMessage(), "Некорректные параметры постраничного отображения");

    }

    @Test
    void getAllFromOtherUsersWrongUserTest() {
        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getAllFromOtherUsers(1L, 0, 20));
        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void getAllFromOtherUsersTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(new ArrayList<>());

        service.getAllFromOtherUsers(1L, 0, 20);

        Mockito.verify(requestRepository, Mockito.times(1))
                .findAllFromOtherUsers(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getRequestTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(request));
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemRequestDto requestDto = service.getRequest(1L, 1L);

        Assertions.assertEquals(requestDto, dto);

    }

    @Test
    void getNotFoundRequestTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(new ArrayList<>());

        final RequestNotFoundException exception =  Assertions.assertThrows(
                RequestNotFoundException.class, () -> service.getRequest(1L, 1L));

        Assertions.assertEquals(exception.getMessage(), "Запрос с id 1 не найден");

    }

    @Test
    void getRequestWrongUserTest() {
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(request));
        Mockito.when(itemRepository.findAllItemsForRequest(Mockito.anyLong())).thenReturn(new ArrayList<>());

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getRequest(1L, 1L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");

    }






}

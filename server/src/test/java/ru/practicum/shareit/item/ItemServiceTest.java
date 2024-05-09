package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.exceptions.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemService service;

    private User user;
    private ItemDto itemDto;

    private Item item;
    private ItemRequest request;

    private final LocalDateTime currentTime = LocalDateTime.now();

    private final LocalDateTime lastStart = LocalDateTime.now().minusSeconds(2);
    private final LocalDateTime lastEnd = LocalDateTime.now().minusSeconds(1);
    private final LocalDateTime nextStart = LocalDateTime.now().plusSeconds(1);
    private final LocalDateTime nextEnd = LocalDateTime.now().plusSeconds(2);

    @BeforeEach
    void setUp() {
        user = new User(1L, "a", "b@ya.ru");
        itemDto = new ItemDto(1L,
                "a",
                "b",
                true,
                user.getId(),
                null,
                null,
                null,
                null);
        item = new Item(1L, "a", "b", true, user, null);
        request = new ItemRequest(1L, "a", user, currentTime);
    }

    @Test
    void addTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto newItem = service.add(itemDto, 1);

        Assertions.assertEquals(newItem, itemDto);
    }

    @Test
    void addWithRequestTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(request));
        itemDto.setRequestId(request.getId());
        ItemDto newItem = service.add(itemDto, 1);

        Assertions.assertEquals(newItem.getId(), itemDto.getId());
    }

    @Test
    void addNullAvailableTest() {
        itemDto.setAvailable(null);
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, 1));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addNullDescriptionTest() {
        itemDto.setDescription(null);
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, 1));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addEmptyNameTest() {
        itemDto.setName("");
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, 1));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addItemWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.add(itemDto, 1));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void updateTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        itemDto.setAvailable(false);
        ItemDto updatedItem = service.update(itemDto, 1L, 1);

        Assertions.assertEquals(updatedItem, itemDto);
    }

    @Test
    void updateNotFoundItemTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.update(itemDto, 1L, 1));

        Assertions.assertEquals(exception.getMessage(), "Сущность с id 1 не найдена");
    }

    @Test
    void noRightsToUpdateTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));

        final NoRightsException exception =  Assertions.assertThrows(
                NoRightsException.class, () -> service.update(itemDto, 1L, 123));

        Assertions.assertEquals("У пользователя с id=123 нет прав для редактирования этого товара",
                exception.getMessage());
    }

    @Test
    void getByIdTest() {
        Booking lastBooking = new Booking(1L,
                lastStart,
                lastEnd,
                item,
                user,
                BookingStatus.APPROVED.name());
        Booking nextBooking = new Booking(1L,
                nextStart,
                nextEnd,
                item,
                user,
                BookingStatus.APPROVED.name());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.findLastBookingForItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(lastBooking));
        Mockito.when(bookingRepository.findNextBookingForItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(nextBooking));
        Mockito.when(commentRepository.getAllCommentsForItem(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemDto currentDto = service.getById(1L, 1L, currentTime);

        Assertions.assertEquals(item.getId(), currentDto.getId());

    }

    @Test
    void getByWrongIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.getById(1L, 1L, currentTime));

        Assertions.assertEquals("Сущность с id 1 не найдена", exception.getMessage());

    }

    @Test
    void getItemsByUserIdTest() {
        service.getItemsByUserId(1L, currentTime);

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByUserId(Mockito.anyLong());
    }

    @Test
    void searchTest() {
        service.search("a");

        Mockito.verify(itemRepository, Mockito.times(1)).search(Mockito.any());
    }

    @Test
    void emptySearchTest() {

        List<ItemDto> dtoList = service.search("");

        Assertions.assertEquals(dtoList, new ArrayList<>());
    }

    @Test
    void addCommentTest() {
        Comment comment = new Comment(1L, "a", item, user, currentTime);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(Collections.singletonList(null));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("a", currentTime);

        CommentDto commentDto = service.addComment(commentCreationDto, 1L, 1L);

        Assertions.assertEquals(commentCreationDto.getText(), commentDto.getText());
    }

    @Test
    void addCommentEmptyTextTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        CommentCreationDto commentCreationDto = new CommentCreationDto("", currentTime);

        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Комментарий не может быть пустым", exception.getMessage());
    }

    @Test
    void addCommentNoRightsTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(new ArrayList<>());
        CommentCreationDto commentCreationDto = new CommentCreationDto("a", currentTime);

        final PostCommentException exception =  Assertions.assertThrows(
                PostCommentException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Вы не можете оставить комментарий", exception.getMessage());
    }

    @Test
    void addCommentWrongItemTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CommentCreationDto commentCreationDto = new CommentCreationDto("a", currentTime);

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Сущность с id 1 не найдена", exception.getMessage());
    }

    @Test
    void addCommentWrongUserTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CommentCreationDto commentCreationDto = new CommentCreationDto("a", currentTime);

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }


}

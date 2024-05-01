package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

public class ItemServiceTest {

    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ItemRequestRepository requestRepository = Mockito.mock(ItemRequestRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    private final ItemService service = new ItemService(itemRepository, userRepository, bookingRepository,
            commentRepository, requestRepository);

    private User user;
    private ItemDto itemDto;

    private Item item;
    private ItemRequest request;

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
        request = new ItemRequest(1L, "a", user, LocalDateTime.now());
    }

    @Test
    void addTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto newItem = service.add(itemDto, "1");

        Assertions.assertEquals(newItem, itemDto);
    }

    @Test
    void addWithRequestTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(request));
        itemDto.setRequestId(request.getId());
        ItemDto newItem = service.add(itemDto, "1");

        Assertions.assertEquals(newItem.getId(), itemDto.getId());
    }

    @Test
    void addNullAvailableTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        itemDto.setAvailable(null);
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, "1"));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addNullDescriptionTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        itemDto.setDescription(null);
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, "1"));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addEmptyNameTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        itemDto.setName("");
        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.add(itemDto, "1"));

        Assertions.assertEquals(exception.getMessage(), "Поля available, name и description не могут быть пустыми");
    }

    @Test
    void addItemWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.add(itemDto, "1"));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void updateTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        itemDto.setAvailable(false);
        ItemDto updatedItem = service.update(itemDto, 1L, "1");

        Assertions.assertEquals(updatedItem, itemDto);
    }

    @Test
    void updateNotFoundItemTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.update(itemDto, 1L, "1"));

        Assertions.assertEquals(exception.getMessage(), "Сущность с id 1 не найдена");
    }

    @Test
    void noRightsToUpdateTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        final NoRightsException exception =  Assertions.assertThrows(
                NoRightsException.class, () -> service.update(itemDto, 1L, "123"));

        Assertions.assertEquals("У пользователя с id=123 нет прав для редактирования этого товара",
                exception.getMessage());
    }

    @Test
    void getByIdTest() {
        Booking lastBooking = new Booking(1L,
                LocalDateTime.now().minusSeconds(2),
                LocalDateTime.now().minusSeconds(1),
                item,
                user,
                BookingStatus.APPROVED.name());
        Booking nextBooking = new Booking(1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item,
                user,
                BookingStatus.APPROVED.name());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.findLastBookingForItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(lastBooking));
        Mockito.when(bookingRepository.findNextBookingForItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(nextBooking));
        Mockito.when(commentRepository.getAllCommentsForItem(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemDto currentDto = service.getById(1L, 1L);

        Assertions.assertEquals(item.getId(), currentDto.getId());

    }

    @Test
    void getByWrongIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(commentRepository.getAllCommentsForItem(Mockito.anyLong())).thenReturn(new ArrayList<>());

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.getById(1L, 1L));

        Assertions.assertEquals("Сущность с id 1 не найдена", exception.getMessage());

    }

    @Test
    void getItemsByUserIdTest() {
        service.getItemsByUserId(1L);

        Mockito.verify(itemRepository, Mockito.times(1)).findAllByUserId(Mockito.anyLong());
    }

    @Test
    void searchTest() {
        service.search("a");

        Mockito.verify(itemRepository, Mockito.times(1)).search(Mockito.any());
    }

    @Test
    void emptySearchTest() {
        Mockito.when(itemRepository.search(Mockito.any())).thenReturn(new ArrayList<>());

        List<ItemDto> dtoList = service.search("");

        Assertions.assertEquals(dtoList, new ArrayList<>());
    }

    @Test
    void addCommentTest() {
        Comment comment = new Comment(1L, "a", item, user, LocalDateTime.now());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(Collections.singletonList(null));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("a");

        CommentDto commentDto = service.addComment(commentCreationDto, 1L, 1L);

        Assertions.assertEquals(commentCreationDto.getText(), commentDto.getText());
    }

    @Test
    void addCommentEmptyTextTest() {
        Comment comment = new Comment(1L, "a", item, user, LocalDateTime.now());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(Collections.singletonList(null));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("");

        final MissingRequiredFieldsException exception =  Assertions.assertThrows(
                MissingRequiredFieldsException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Комментарий не может быть пустым", exception.getMessage());
    }

    @Test
    void addCommentNoRightsTest() {
        Comment comment = new Comment(1L, "a", item, user, LocalDateTime.now());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("a");

        final PostCommentException exception =  Assertions.assertThrows(
                PostCommentException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Вы не можете оставить комментарий", exception.getMessage());
    }

    @Test
    void addCommentWrongItemTest() {
        Comment comment = new Comment(1L, "a", item, user, LocalDateTime.now());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("a");

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Сущность с id 1 не найдена", exception.getMessage());
    }

    @Test
    void addCommentWrongUserTest() {
        Comment comment = new Comment(1L, "a", item, user, LocalDateTime.now());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.findAllBookingsForItemAndUserByEndTime(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentCreationDto commentCreationDto = new CommentCreationDto("a");

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.addComment(commentCreationDto, 1L, 1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }


}

package ru.practicum.shareit.booking;

import net.bytebuddy.asm.Advice;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingServiceTest {

    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private final LocalDateTime start = LocalDateTime.now().plusSeconds(1);
    private final LocalDateTime end = LocalDateTime.now().plusSeconds(2);

    private final BookingService service = new BookingService(bookingRepository, itemRepository, userRepository);
    private User user = new User(1L, "a", "b@ya.ru");
    private Item item = new Item(1L, "a","b", true, user, null);
    private BookingCreationDto bookingCreationDto = new BookingCreationDto(
            1L,
            1L,
            1L,
            start,
            end);

    private Booking booking = new Booking(1L, start,
            end, item, user, BookingStatus.WAITING.name());

    private BookingDto bookingDto = new BookingDto(1L, start,
            end, item, user, BookingStatus.WAITING.name());

    @Test
    void addTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto dto = service.add(bookingCreationDto, 2L);

        Assertions.assertEquals(dto, bookingDto);
    }

    @Test
    void addWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.add(bookingCreationDto, 2L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void addBookingForWrongItemTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        final ItemNotFoundException exception =  Assertions.assertThrows(
                ItemNotFoundException.class, () -> service.add(bookingCreationDto, 2L));

        Assertions.assertEquals(exception.getMessage(), "Предмет с id 1 не найден");
    }

    @Test
    void addBookingWithoutTimeTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        bookingCreationDto.setEnd(null);
        bookingCreationDto.setStart(null);
        final IncorrectBookingTimeException exception =  Assertions.assertThrows(
                IncorrectBookingTimeException.class, () -> service.add(bookingCreationDto, 2L));

        Assertions.assertEquals("Необходимо указать время бронирования", exception.getMessage());
    }

    @Test
    void addBookingEndEqualsStartTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        bookingCreationDto.setEnd(LocalDateTime.now());
        bookingCreationDto.setStart(LocalDateTime.now());
        final IncorrectBookingTimeException exception =  Assertions.assertThrows(
                IncorrectBookingTimeException.class, () -> service.add(bookingCreationDto, 2L));

        Assertions.assertEquals("Некорректно указано время бронирования", exception.getMessage());
    }

    @Test
    void addBookingNotAvailableItemTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        booking.getItem().setAvailable(false);
        final UnavailableItemException exception =  Assertions.assertThrows(
                UnavailableItemException.class, () -> service.add(bookingCreationDto, 2L));

        Assertions.assertEquals("Предмет недоступен для бронирования", exception.getMessage());
    }

    @Test
    void addBookingForOwnItemTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        final NoRightsException exception =  Assertions.assertThrows(
                NoRightsException.class, () -> service.add(bookingCreationDto, 1L));

        Assertions.assertEquals("Вы не можете забронировать свой предмет", exception.getMessage());
    }


    @Test
    void updateTest() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto dto = service.update(1L, 1L, "REJECTED");

        Assertions.assertEquals(dto.getId(), bookingDto.getId());
    }

    @Test
    void updateWrongBookingTest() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        final BookingNotFoundException exception =  Assertions.assertThrows(
                BookingNotFoundException.class, () -> service.update(1L, 1L, "REJECTED"));

        Assertions.assertEquals("Бронь с id 1 не найдена", exception.getMessage());
    }

    @Test
    void updateByWrongUserTest() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        final NoRightsException exception =  Assertions.assertThrows(
                NoRightsException.class, () -> service.update(1L, 123L, "REJECTED"));

        Assertions.assertEquals("У пользователя с id 123 нет прав для подтверждения бронирования",
                exception.getMessage());
    }

    @Test
    void updateAlreadyApprovedBookingTest() {
        booking.setStatus(BookingStatus.APPROVED.name());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        final IncorrectActionException exception =  Assertions.assertThrows(
                IncorrectActionException.class, () -> service.update(1L, 1L, "true"));

        Assertions.assertEquals("Бронирование уже подтверждено", exception.getMessage());
    }

    @Test
    void getAllByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllOrderByEnd(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByState(1L, "ALL", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getPastByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByEndIsBefore(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByState(1L, "PAST", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getCurrentByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByEndIsAfterAndStartIsBefore(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByState(1L, "CURRENT", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getFutureByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByStartIsAfter(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByState(1L, "FUTURE", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getRejectedByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByStatus(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByState(1L, "REJECTED", null, null);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getByStateFowWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.findAllByStatus(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getAllByState(1L, "REJECTED", 0, 2));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void getByUnsupportedStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByStatus(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class, () ->
                        service.getAllByState(1L, "UNSUPPORTED", 0, 2));

        Assertions.assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());
    }

    @Test
    void getForOwnerByUnsupportedStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByStatus(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class, () ->
                        service.getAllByOwnerAndState(1L, "UNSUPPORTED", 0, 2));

        Assertions.assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());
    }

    @Test
    void getForOwnerByStateForWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.findAllForOwnerByStatus(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class, () ->
                        service.getAllByOwnerAndState(1L, "REJECTED", 0, 2));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void getAllForOwnerByStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByOwner(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByOwnerAndState(1L, "ALL", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getAllForOwnerByCurrentStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByCurrentStateFowOwner(Mockito.any(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByOwnerAndState(1L, "Current", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getAllForOwnerByFutureStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByStartIsAfter(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByOwnerAndState(1L, "FUTURE", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getAllForOwnerByPastStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllByEndIsBefore(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByOwnerAndState(1L, "PAST", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getAllForOwnerByRejectedStateTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findAllForOwnerByStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<BookingDto> bookingDtos = service.getAllByOwnerAndState(1L, "REJECTED", 0, 20);

        Assertions.assertEquals(bookingDtos, new ArrayList<>());
    }

    @Test
    void getByIdTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingDto dto = service.getById(1L, 1L);

        Assertions.assertEquals(dto, bookingDto);
    }

    @Test
    void getByIdForWrongUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getById(1L, 1L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id 1 не найден");
    }

    @Test
    void getByIdForNotExistsBookingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final BookingNotFoundException exception =  Assertions.assertThrows(
                BookingNotFoundException.class, () -> service.getById(1L, 1L));

        Assertions.assertEquals(exception.getMessage(), "Бронь с id 1 не найдена");
    }

    @Test
    void getByIdForNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

        final BookingNotFoundException exception =  Assertions.assertThrows(
                BookingNotFoundException.class, () -> service.getById(1L, 123L));

        Assertions.assertEquals(exception.getMessage(), "У пользователя нет активных бронирований");
    }




}

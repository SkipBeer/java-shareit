package ru.practicum.shareit.booking;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.awt.print.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto add(BookingCreationDto creationDto, Long sharerId) {
        Booking booking = BookingMapper.fromBookingCreationDto(creationDto);
        booking.setBooker(userRepository.findById(sharerId).orElseThrow(()
                -> {throw new UserNotFoundException("Пользователь с id " + creationDto.getUserId() + " не найден");}));
        booking.setItem(itemRepository.findById(creationDto.getItemId()).orElseThrow(()
                -> {throw new ItemNotFoundException("Предмет с id " + creationDto.getItemId() + " не найден");}));
        validate(booking, sharerId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto update(Long bookingId, Long sharerId, String approvedStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> {throw new BookingNotFoundException("Бронь с id " + bookingId + " не найдена");});
        if (!sharerId.equals(booking.getItem().getUserId())) {
            throw new NoRightsException("У пользователя с id "+ sharerId +" нет прав для подтверждения бронирования");
        }
        if (Boolean.parseBoolean(approvedStatus)) {
            if (booking.getStatus().equals(BookingStatus.APPROVED.name())){
                throw new IncorrectActionException("Бронирование уже подтверждено");
            }
            booking.setStatus(BookingStatus.APPROVED.name());
        }
        if (!Boolean.parseBoolean(approvedStatus)) {
            booking.setStatus(BookingStatus.REJECTED.name());
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getById(Long bookingId, Long sharerId) {
        userRepository.findById(sharerId).orElseThrow(()
                -> {throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден");});
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> {throw new BookingNotFoundException("Бронь с id " + bookingId + " не найдена");});
        if (!booking.getItem().getUserId().equals(sharerId) && !booking.getBooker().getId().equals(sharerId)) {
            throw new BookingNotFoundException("У пользователя нет активных бронирований");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getAllByState(Long sharerId, String state) {
        userRepository.findById(sharerId).orElseThrow(()
                -> {throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден");});

        if (Arrays.stream(BookingState.values()).noneMatch(existsState -> existsState.name().equals(state))) {
            throw new UnsupportedStatusException("Unknown state: " + state.toUpperCase());
        }
        if (state.toUpperCase().equals(BookingState.ALL.name())) {
            return bookingRepository.findAllOrderByEnd().stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.CURRENT.name())) {
            return bookingRepository.findAllByEndIsAfterAndStartIsBefore(LocalDateTime.now(), LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.REJECTED.name()) ||
                state.toUpperCase().equals(BookingState.WAITING.name())) {
            return bookingRepository.findAllByStatus(state, sharerId)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.FUTURE.name())) {
            return bookingRepository.findAllByStartIsAfter(LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.PAST.name())) {
            return bookingRepository.findAllByEndIsBefore(LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return null;
    }

    public List<BookingDto> getAllByOwnerAndState(Long sharerId, String state) {
        if (Arrays.stream(BookingState.values()).noneMatch(existsState -> existsState.name().equals(state))) {
            throw new UnsupportedStatusException("Unknown state: " + state.toUpperCase());

        }
        userRepository.findById(sharerId).orElseThrow(()
                -> {throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден");});
        if (state.toUpperCase().equals(BookingState.ALL.name())) {
            return bookingRepository.findAllByOwner(sharerId).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.CURRENT.name())) {
            return bookingRepository.findAllByEndIsAfterAndStartIsBefore(LocalDateTime.now(), LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.REJECTED.name()) ||
                state.toUpperCase().equals(BookingState.WAITING.name())) {
            return bookingRepository.findAllForOwnerByStatus(state, sharerId)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.FUTURE.name())) {
            return bookingRepository.findAllByStartIsAfter(LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        if (state.toUpperCase().equals(BookingState.PAST.name())) {
            return bookingRepository.findAllByEndIsBefore(LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return null;
    }

    private void validate(Booking booking, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (booking.getEnd() == null || booking.getStart() == null) {
            throw new IncorrectBookingTimeException("Необходимо указать время бронирования");
        }

        if (booking.getStart().equals(booking.getEnd()) || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd()) || booking.getEnd().isBefore(LocalDateTime.now())
                || booking.getStart().isBefore(LocalDateTime.now()))
        {
            throw new IncorrectBookingTimeException("Некорректно указано время бронирования");
        }

        if (booking.getItem() == null) {
            throw new ItemNotFoundException("Предмет с id не найден");
        }

        if (!booking.getItem().getAvailable()) {
            throw new UnavailableItemException("Предмет недоступен для бронирования");
        }

        if (userId.equals(booking.getItem().getUserId())) {
            throw new NoRightsException("Вы не можете забронировать свой предмет");
        }

    }
}

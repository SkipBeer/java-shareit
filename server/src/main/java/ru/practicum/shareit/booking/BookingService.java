package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.exceptions.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.Collections;
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
                -> {
            throw new UserNotFoundException("Пользователь с id " + creationDto.getUserId() + " не найден"); }));
        booking.setItem(itemRepository.findById(creationDto.getItemId()).orElseThrow(()
                -> {
            throw new ItemNotFoundException("Предмет с id " + creationDto.getItemId() + " не найден"); }));
        validate(booking, sharerId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto update(Long bookingId, Long sharerId, String approvedStatus) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> {
            throw new BookingNotFoundException("Бронь с id " + bookingId + " не найдена"); });
        if (!sharerId.equals(booking.getItem().getUser().getId())) {
            throw new NoRightsException("У пользователя с id " + sharerId + " нет прав для подтверждения бронирования");
        }
        if (Boolean.parseBoolean(approvedStatus)) {
            if (booking.getStatus().equals(BookingStatus.APPROVED.name())) {
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
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> {
            throw new BookingNotFoundException("Бронь с id " + bookingId + " не найдена"); });
        if (!booking.getItem().getUser().getId().equals(sharerId) && !booking.getBooker().getId().equals(sharerId)) {
            throw new BookingNotFoundException("У пользователя нет активных бронирований");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getAllByState(Long sharerId, String state, Integer from, Integer size, LocalDateTime time) {
        userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });

        if (Arrays.stream(BookingState.values()).noneMatch(existsState -> existsState.name().equals(state))) {
            throw new UnsupportedStatusException("Unknown state: " + state.toUpperCase());
        }
        switch (BookingState.valueOf(state.toUpperCase())) {
            case ALL:
                return bookingRepository.findAllOrderByEnd(sharerId, createPage(from, size)).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByEndIsBefore(time,createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByEndIsAfterAndStartIsBefore(time, time,createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByStartIsAfter(time,createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
            case WAITING:
                return bookingRepository.findAllByStatus(state, sharerId, createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    public List<BookingDto> getAllByOwnerAndState(Long sharerId, String state, Integer from, Integer size, LocalDateTime time) {
        if (Arrays.stream(BookingState.values()).noneMatch(existsState -> existsState.name().equals(state.toUpperCase()))) {
            throw new UnsupportedStatusException("Unknown state: " + state.toUpperCase());

        }
        userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });

        switch (BookingState.valueOf(state.toUpperCase())) {
            case ALL:
                return bookingRepository.findAllByOwner(sharerId, createPage(from, size)).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByCurrentStateFowOwner(time, sharerId, createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByStartIsAfter(time, createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByEndIsBefore(time, createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllForOwnerByStatus(state, sharerId, createPage(from, size))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    private void validate(Booking booking, Long userId) {

        if (!booking.getItem().getAvailable()) {
            throw new UnavailableItemException("Предмет недоступен для бронирования");
        }

        if (userId.equals(booking.getItem().getUser().getId())) {
            throw new NoRightsException("Вы не можете забронировать свой предмет");
        }

    }

    private PageRequest createPage(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }
        if (from < 0) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        }
        if (size < 0) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        }
        if (from == 0 && size == 0) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        }
        int page = from / size;
        return PageRequest.of(page, size);
    }
}


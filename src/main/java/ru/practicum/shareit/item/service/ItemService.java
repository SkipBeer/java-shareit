package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.exceptions.MissingRequiredFieldsException;
import ru.practicum.shareit.exceptions.exceptions.NoRightsException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    public ItemDto add(ItemDto item, String owner) {
        item.setOwner(Long.parseLong(owner));
        validate(item);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.fromItemDto(item)));
    }

    public ItemDto update(ItemDto patch, Long itemId, String sharerId) {
        Item existsItem = itemRepository.findById(itemId).
                orElseThrow(() -> {throw new ItemNotFoundException("Сущность с id " + itemId + " не найден");});
        if (existsItem.getUserId() != Long.parseLong(sharerId)) {
            throw new NoRightsException("У пользователя с id=" + sharerId + " нет прав для редактирования этого товара");
        }
        customApplyPatchToItem(patch, existsItem);
        return ItemMapper.toItemDto(itemRepository.save(existsItem));
    }

    public ItemDto getById(Long itemId, Long sharerId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Сущность с id " + itemId + " не найден");}));
        if (itemDto.getOwner().equals(sharerId)) {
            setBookingsForItem(itemDto);
        }
        return itemDto;
    }

    public List<ItemDto> getItemsByUserId(Long sharerId) {
        return itemRepository.findAllByUserId(sharerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .peek(this::setBookingsForItem)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String searchText) {
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(searchText.toLowerCase())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDto addComment(CommentCreationDto creationDto, Long sharerId, Long itemId){
        Comment comment = new Comment(null, creationDto.getText(),
                itemRepository.findById(itemId).
                        orElseThrow(()
                                -> {throw new ItemNotFoundException("Сущность с id " + itemId + " не найдена");}),
                userRepository.findById(sharerId).orElseThrow(()
                        -> {throw new UserNotFoundException("Пользователь с id " + itemId + " не найден");}),
                LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }


    private void validate(ItemDto item) {
        if (item.getAvailable() == null || item.getName().isEmpty() || item.getDescription() == null) {
            throw new MissingRequiredFieldsException("Поля available, name и description не могут быть пустыми");
        }
        if (userRepository.findById(item.getOwner()).isEmpty()) {
            throw new UserNotFoundException("Владелец с id " + item.getOwner() + " не найден");
        }
    }

    private void validateComment(Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new MissingRequiredFieldsException("Комментарий не может быть пустым");
        }
        List<Booking> bookings = bookingRepository.findAllBookingsForItem(comment.getItem().getId()).stream()
                .filter(booking -> booking.getBooker().getId().equals(comment.getAuthor().getId()))
                .filter(booking -> booking.getItem().getId().equals(comment.getItem().getId())).collect(Collectors.toList());
        if (bookings.stream().findAny().isEmpty() ||
                bookings.stream()
                        .filter(booking -> booking.getEnd().isAfter(comment.getCreated())).findAny().isEmpty()) {
            throw new NoRightsException("Вы не можете оставить комментарий");
        }
    }

    private void customApplyPatchToItem(ItemDto patch, Item targetItem) {
        if (patch.getName() != null && !patch.getName().isEmpty()) {
            targetItem.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            targetItem.setDescription(patch.getDescription());
        }
        if (!targetItem.getAvailable().equals(patch.getAvailable()) && patch.getAvailable() != null) {
            targetItem.setAvailable(patch.getAvailable());
        }
    }

    private void setBookingsForItem(ItemDto itemDto) {
        try {
            itemDto.setLastBooking(BookingMapper.toBookingItemDto(bookingRepository
                    .findLastBookingForItem(LocalDateTime.now(), itemDto.getId()).stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED.name()))
                    .findFirst().get()));
            itemDto.setNextBooking(BookingMapper.toBookingItemDto(bookingRepository
                    .findNextBookingForItem(LocalDateTime.now(), itemDto.getId()).stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED.name()))
                    .findFirst().get()));
        } catch (NoSuchElementException e) {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
        }
    }


}

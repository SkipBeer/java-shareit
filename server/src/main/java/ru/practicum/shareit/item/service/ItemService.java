package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.exceptions.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository requestRepository;

    public ItemDto add(ItemDto itemDto, long owner) {
        itemDto.setOwner(owner);
        validate(itemDto);
        Item newItem = ItemMapper.fromItemDto(itemDto);
        newItem.setUser(userRepository.findById(owner).orElseThrow(()
                        -> {
                    throw new UserNotFoundException("Пользователь с id " + owner + " не найден"); }));
        setRequestForItem(newItem, itemDto.getRequestId());
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    public ItemDto update(ItemDto patch, Long itemId, long sharerId) {
        Item existsItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> {
                            throw new ItemNotFoundException("Сущность с id " + itemId + " не найдена"); });
        if (existsItem.getUser().getId() != sharerId) {
            throw new NoRightsException("У пользователя с id=" + sharerId + " нет прав для редактирования этого товара");
        }
        customApplyPatchToItem(patch, existsItem);
        return ItemMapper.toItemDto(itemRepository.save(existsItem));
    }

    public ItemDto getById(Long itemId, Long sharerId, LocalDateTime time) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException("Сущность с id " + itemId + " не найдена"); }));
        if (itemDto.getOwner().equals(sharerId)) {
            setBookingsForItem(itemDto, time);
        }
        itemDto.setComments(commentRepository.getAllCommentsForItem(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    public List<ItemDto> getItemsByUserId(Long sharerId, LocalDateTime time) {
        return itemRepository.findAllByUserId(sharerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> setBookingsForItem(itemDto, time))
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

    public CommentDto addComment(CommentCreationDto creationDto, Long sharerId, Long itemId) {
        Comment comment = new Comment(null, creationDto.getText(),
                itemRepository.findById(itemId)
                                .orElseThrow(()
                                -> {
                                    throw new ItemNotFoundException("Сущность с id " + itemId + " не найдена"); }),
                userRepository.findById(sharerId).orElseThrow(()
                        -> {
                    throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); }),
                creationDto.getCreated());
        validateComment(comment);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }


    private void validate(ItemDto item) {
        if (item.getAvailable() == null || item.getName().isEmpty() || item.getDescription() == null) {
            throw new MissingRequiredFieldsException("Поля available, name и description не могут быть пустыми");
        }
    }

    private void validateComment(Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new MissingRequiredFieldsException("Комментарий не может быть пустым");
        }
        List<Booking> bookings = bookingRepository.findAllBookingsForItemAndUserByEndTime(comment.getItem().getId(),
            comment.getAuthor().getId(), comment.getCreated());
        if (bookings.size() == 0) {
            throw new PostCommentException("Вы не можете оставить комментарий");
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

    private void setBookingsForItem(ItemDto itemDto, LocalDateTime time) {

        Optional<Booking> optLastBooking = bookingRepository
                .findLastBookingForItem(time, itemDto.getId()).stream()
                .findFirst();
        if (optLastBooking.isPresent()) {
            itemDto.setLastBooking(BookingMapper.toBookingItemDto(optLastBooking.get()));
        } else {
            itemDto.setLastBooking(null);
        }

        Optional<Booking> optNextBooking = bookingRepository
                .findNextBookingForItem(time, itemDto.getId()).stream()
                .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED.name()))
                .findFirst();
        if (optNextBooking.isPresent()) {
            itemDto.setNextBooking(BookingMapper.toBookingItemDto(optNextBooking.get()));
        } else {
            itemDto.setNextBooking(null);
        }
    }

    private void setRequestForItem(Item item, Long requestId) {
        if (requestId != null) {
            Optional<ItemRequest> request = requestRepository.findById(requestId);
            request.ifPresent(item::setRequest);
        } else {
            item.setRequest(null);
        }
    }
}
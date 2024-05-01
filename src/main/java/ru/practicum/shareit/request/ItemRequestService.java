package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.exceptions.IncorrectRequestParamException;
import ru.practicum.shareit.exceptions.exceptions.MissingRequiredFieldsException;
import ru.practicum.shareit.exceptions.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


    public ItemRequestDto add(ItemRequestCreationDto dto, Long sharerId) {
        ItemRequest request = ItemRequestMapper.fromCreationDto(dto);
        validate(request, sharerId);
        return ItemRequestMapper.toDto(requestRepository.save(request));
    }

    public List<ItemRequestDto> getForUser(Long sharerId) {

        userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });

        return requestRepository.findByRequestorId(sharerId).stream()
                .map(ItemRequestMapper::toDto)
                .peek(this::setItemsForRequest)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllFromOtherUsers(Long sharerId, Integer from, Integer size) {
        if (from == null & size == null) {
            return new ArrayList<>();
        }
        validatePagination(from, size);
        Pageable pageable = PageRequest.of(from, size);
        userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });
        return requestRepository.findAllFromOtherUsers(sharerId, pageable).stream()
                .map(ItemRequestMapper::toDto)
                .peek(this::setItemsForRequest)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequest(Long sharerId, Long requestId) {
        userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); });
        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(requestRepository.findById(requestId).orElseThrow(()
                -> {
            throw new RequestNotFoundException("Запрос с id " + requestId + " не найден"); }));
        setItemsForRequest(itemRequestDto);
        return itemRequestDto;
    }


    private void validate(ItemRequest request, Long sharerId) {

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new MissingRequiredFieldsException("Описание запроса не может быть пустым");
        }

        request.setRequestor(userRepository.findById(sharerId).orElseThrow(()
                -> {
            throw new UserNotFoundException("Пользователь с id " + sharerId + " не найден"); }));
    }

    private void validatePagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        } else if ((from == 0 && size == 0)) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        }
    }

    private void setItemsForRequest(ItemRequestDto request) {
        List<Item> items = itemRepository.findAllItemsForRequest(request.getId());

        if (items.size() == 0) {
            request.setItems(new ArrayList<>());
        }
        request.setItems(items.stream().map(ItemMapper::toRequestDto).collect(Collectors.toList()));
    }

}

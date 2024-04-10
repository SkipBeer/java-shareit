package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.exceptions.InvalidEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto add(UserDto user) {
        validate(user, null);
        return UserMapper.toUserDto(userRepository.add(UserMapper.fromUserDto(user)));
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    public UserDto update(Long id, UserDto patch)  {
        User existsUser = userRepository.getById(id);
        customApplyPatchToUser(patch, existsUser);
        return UserMapper.toUserDto(userRepository.update(id, existsUser));
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private void customApplyPatchToUser(UserDto patch, User targetUser) {
        if (patch.getEmail() != null) {
            validate(patch, targetUser.getId());
            targetUser.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            targetUser.setName(patch.getName());
        }
    }

    private void validate(UserDto user, Long id) {
        if (user.getEmail() == null) {
            throw new InvalidEmailException("email не может быть пустым");
        }

        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("email должен соответствовать формату example@example.com");
        }

        for (User existsUser : userRepository.getAll()) {

            if (existsUser.getId().equals(id)) {
                continue;
            }

            if (existsUser.getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistsException("пользователь с таким email уже сущестует");
            }
        }
    }
}

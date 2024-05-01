package ru.practicum.shareit.user;


import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.exceptions.InvalidEmailException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceTest {

    private UserRepository repository = Mockito.mock(UserRepository.class);
    private UserService service = new UserService(repository);
    private UserDto userDto;
    private User user;

    private List<UserDto> dtoList;

    @BeforeEach
    private void setUp() {
        userDto = new UserDto(1L, "a", "b@ya.ru");
        user =  new User(1L, "a", "b@ya.ru");
        dtoList = new ArrayList<>();
        dtoList.add(userDto);
    }

    @Test
    public void addUserTest() {
        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
        UserDto dto = service.add(userDto);

        Assertions.assertEquals(dto, userDto);
    }

    @Test
    public void failedGetByIdTest() {
        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getById(Mockito.anyLong()));
        Assertions.assertEquals("Пользователь с id 0 не найден", exception.getMessage());
    }

    @Test
    public void createUserWrongEmailTest() {
        UserDto dto = new UserDto(2L, "a", "");

        final InvalidEmailException exception =  Assertions.assertThrows(
                InvalidEmailException.class, () -> service.add(dto));
        Assertions.assertEquals("Некорректный email", exception.getMessage());
    }

    @Test
    public void createUserNullEmailTest() {
        UserDto dto = new UserDto(2L, "a", null);

        final InvalidEmailException exception =  Assertions.assertThrows(
                InvalidEmailException.class, () -> service.add(dto));
        Assertions.assertEquals("Некорректный email", exception.getMessage());
    }

    @Test
    public void updateUserTest() {
        UserDto patch = new UserDto(1L, "patch", "patch@mail.ru");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
        service.update(1L, patch);
    }

    @Test
    public void updateUserWrongEmailTest() {
        UserDto patch = new UserDto(1L, "patch", "patchmail.ru");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
        final InvalidEmailException exception =  Assertions.assertThrows(
                InvalidEmailException.class, () -> service.update(1L, patch));
        Assertions.assertEquals("email должен соответствовать формату example@example.com",
                exception.getMessage());
    }

    @Test
    public void updateUserDuplicateEmailTest() {
        User user1 = new User(2L, "patch", "c@ya.ru");
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user1);
        UserDto patch = new UserDto(1L, "patch", "c@ya.ru");
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
        Mockito.when(repository.findAll()).thenReturn(users);
        final EmailAlreadyExistsException exception =  Assertions.assertThrows(
                EmailAlreadyExistsException.class, () -> service.update(1L, patch));
        Assertions.assertEquals("пользователь с таким email уже сущестует", exception.getMessage());
    }

    @Test
    public void deleteByIdTest() {
        service.deleteById(1L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    public void getAllTest() {
        List<User> users = new ArrayList<>();
        users.add(user);
        Mockito.when(repository.findAll()).thenReturn(users);

        List<UserDto> list = service.getAll();

        Assertions.assertEquals(list, dtoList);
    }

    @Test
    public void getNotExistsUserTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception =  Assertions.assertThrows(
                UserNotFoundException.class, () -> service.getById(10L));

        Assertions.assertEquals(exception.getMessage(), "Пользователь с id " + 10 + " не найден");
    }

}

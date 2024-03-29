package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    User add(User user);

    User getById(Long id);

    User update(Long id, User user);

    void deleteById(Long id);

    List<User> getAll();

}

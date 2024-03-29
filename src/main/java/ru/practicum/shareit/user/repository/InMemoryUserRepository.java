package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.exceptions.InvalidEmailException;
import ru.practicum.shareit.exceptions.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;


import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    private long generatorId = 1;

    private long generateId() {
        return generatorId++;
    }

    @Override
    public User add(User user) {
        validate(user, null);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с указанным id не найден");
        }
        return user;
    }

    @Override
    public User update(Long id, User user) {
        validate(user, id);
        users.put(id, user);
        return getById(id);
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private void validate(User user, Long id) {
        if (user.getEmail() == null) {
            throw new InvalidEmailException("email не может быть пустым");
        }

        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("email должен соответствовать формату example@example.com");
        }

        for (User existsUser : users.values()) {

            if (existsUser.getId().equals(id)) {
                continue;
            }

            if (existsUser.getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistsException("пользователь с таким email уже сущестует");
            }
        }
    }
}
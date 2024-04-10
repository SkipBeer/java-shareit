package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
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
}
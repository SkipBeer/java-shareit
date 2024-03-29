package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        return userRepository.add(user);
    }

    public User getById(Long id) {
        return userRepository.getById(id);
    }

    public User update(Long id, User patch)  {
        User existsUser = userRepository.getById(id);
        User targetUser = new User(id, existsUser.getName(), existsUser.getEmail());
        User updatedUser =  customApplyPatchToUser(patch, targetUser);
        return userRepository.update(id, updatedUser);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    private User customApplyPatchToUser(User patch, User targetUser) {
        if (patch.getEmail() != null) {
            targetUser.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            targetUser.setName(patch.getName());
        }
        return targetUser;
    }
}

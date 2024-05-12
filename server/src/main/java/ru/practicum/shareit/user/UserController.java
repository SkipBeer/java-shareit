package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto user) {
        log.info("Creating user {}", user);
        return userService.add(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto patch, @PathVariable Long id) {
        log.info("Updating user by id={} patch={}", id, patch);
        return userService.update(id, patch);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Getting user by id={}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Getting all users");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Delete users with id={}", id);
        userService.deleteById(id);
    }

}

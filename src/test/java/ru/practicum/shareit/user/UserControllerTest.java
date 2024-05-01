package ru.practicum.shareit.user;


import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserControllerTest {

    private UserService userService = Mockito.mock(UserService.class);

    private UserController userController = new UserController(userService);

    private UserDto userDto;

    @BeforeEach
    private void setUp() {
        userDto = new UserDto(1L, "a", "b@ya.ru");
    }

    @Test
    public void addUserTest() {
        Mockito.when(userService.add(Mockito.any())).thenReturn(userDto);
        UserDto newUser = userController.addUser(userDto);

        Assertions.assertEquals(this.userDto, newUser);

    }

    @Test
    public void updateUserTest() {
        this.userDto.setName("newName");

        Mockito.when(userService.update(this.userDto.getId(),userDto)).thenReturn(this.userDto);
        UserDto updatedUser = userController.update(userDto, userDto.getId());

        Assertions.assertEquals(userDto, updatedUser);

    }

    @Test
    public void getAllTest() {
        UserDto userDto1 = new UserDto(1L, "user1", "1");
        UserDto userDto2 = new UserDto(2L, "user2", "2");
        UserDto userDto3 = new UserDto(3L, "user3", "3");
        List<UserDto> users = new ArrayList<>();
        users.add(userDto1);
        users.add(userDto2);
        users.add(userDto3);
        Mockito.when(userService.getAll()).thenReturn(users);

        List<UserDto> existsUsers = userController.getAll();

        Assertions.assertEquals(users, existsUsers);
    }

    @Test
    public void deleteByIdTest() {
        userController.deleteById(userDto.getId());
        Mockito.verify(userService, Mockito.times(1)).deleteById(Mockito.anyLong());

    }

    @Test
    public void getByIdTest() {
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(userDto);

        UserDto dto = userController.getUserById(userDto.getId());

        Assertions.assertEquals(userDto, dto);
    }


}

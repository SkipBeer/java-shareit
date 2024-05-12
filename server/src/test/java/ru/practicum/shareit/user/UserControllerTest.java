package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private UserDto userDto;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
        userDto = new UserDto(1L, "a", "b@ya.ru");
    }



    @Test
    public void addUserTest() throws Exception {
        Mockito.when(userService.add(Mockito.any())).thenReturn(userDto);
        UserDto newUser = userController.addUser(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Assertions.assertEquals(userDto, newUser);

    }

    @Test
    public void updateUserTest() throws Exception {
        this.userDto.setName("newName");

        Mockito.when(userService.update(this.userDto.getId(),userDto)).thenReturn(this.userDto);
        UserDto updatedUser = userController.update(userDto, userDto.getId());

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Assertions.assertEquals(userDto, updatedUser);

    }

    @Test
    public void getAllTest() throws Exception {
        UserDto userDto1 = new UserDto(1L, "user1", "1");
        UserDto userDto2 = new UserDto(2L, "user2", "2");
        UserDto userDto3 = new UserDto(3L, "user3", "3");
        List<UserDto> users = new ArrayList<>();
        users.add(userDto1);
        users.add(userDto2);
        users.add(userDto3);
        Mockito.when(userService.getAll()).thenReturn(users);

        List<UserDto> existsUsers = userController.getAll();

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(users, existsUsers);
    }

    @Test
    public void deleteByIdTest() throws Exception {
        userController.deleteById(userDto.getId());
        Mockito.verify(userService, Mockito.times(1)).deleteById(Mockito.anyLong());

        mvc.perform(delete("/users/{id}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void getByIdTest() throws Exception {
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(userDto);

        UserDto dto = userController.getUserById(userDto.getId());

        mvc.perform(get("/users/{id}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Assertions.assertEquals(userDto, dto);
    }


}

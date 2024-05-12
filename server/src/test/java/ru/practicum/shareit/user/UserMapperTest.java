package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;


public class UserMapperTest {

    @Test
    public void toUserDtoTest() {
        User user = new User(null, "a", "b@ya.ru");

        UserDto dto = UserMapper.toUserDto(user);

        Assertions.assertEquals(user.getId(), dto.getId());
        Assertions.assertEquals(user.getName(), dto.getName());
        Assertions.assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void fromUserDtoTest() {
        UserDto dto = new UserDto(null, "a", "b@ya.ru");

        User user = UserMapper.fromUserDto(dto);

        Assertions.assertEquals(user.getId(), dto.getId());
        Assertions.assertEquals(user.getName(), dto.getName());
        Assertions.assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void mapperConstructorTest() {
        UserMapper mapper = new UserMapper();
        Assertions.assertNotEquals(null, mapper);
    }
}

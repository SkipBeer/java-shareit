package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapperTest {

    @Test
    void commentMapperConstructorTest() {
        User user = new User(1L, "a", "b@ya.ru");
        Item item = new Item(1L, "a", "b", true, user, null);
        Comment comment = new Comment(1L, "a", item, user, null);

        CommentMapper commentMapper = new CommentMapper();

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Assertions.assertNotEquals(commentMapper, null);
        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals(comment.getText(), commentDto.getText());

    }

    @Test
    void itemMapperConstructorTest() {
        ItemDto dto = new ItemDto(
                1L,
                "a",
                "b",
                true,
                1L,
                1L,
                null,
                null,
                null);
        ItemMapper itemMapper = new ItemMapper();

        Item item = ItemMapper.fromItemDto(dto);

        Assertions.assertNotEquals(itemMapper, null);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getName(), dto.getName());
        Assertions.assertEquals(item.getDescription(), dto.getDescription());

    }
}

package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

public class ItemMapperTest {

    @Test
    void CommentMapperConstructorTest() {
        CommentMapper commentMapper = new CommentMapper();

        Assertions.assertNotEquals(commentMapper, null);
    }

    @Test
    void ItemMapperConstructorTest() {
        ItemMapper itemMapper = new ItemMapper();

        Assertions.assertNotEquals(itemMapper, null);
    }
}

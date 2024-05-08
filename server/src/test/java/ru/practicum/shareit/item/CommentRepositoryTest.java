package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    Comment comment;

    Item item;

    @BeforeEach
    void setUp() {
        item = new Item(null, "a", "b", true, null, null);
        comment = new Comment(null, "a", item, null, null);
        Long itemId = itemRepository.save(item).getId();
        Long commentId = commentRepository.save(comment).getId();
        item.setId(itemId);
        comment.setId(commentId);
    }

    @AfterEach
    void tearDown() {
        commentRepository.delete(comment);
        itemRepository.delete(item);
    }

    @Test
    void getAllCommentsForItemTest() {
        List<Comment> commentList = commentRepository.getAllCommentsForItem(item.getId());
        Assertions.assertNotNull(commentList);
        Assertions.assertEquals(commentList.get(0), comment);
    }

}

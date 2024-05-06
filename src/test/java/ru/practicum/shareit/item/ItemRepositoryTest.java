package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;

    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "a", "b@ya.ru");
        item = new Item(1L, "a", "b", true, null, null);
        Long itemId = repository.save(item).getId();
        item.setId(itemId);
    }

    @AfterEach
    void terDown() {
        repository.delete(item);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void findAllByUserIdTest() {
        List<Item> items = repository.findAllByUserId(1L);
        Assertions.assertNotNull(items);
        Assertions.assertEquals(0, items.size());
    }

    @Test
    void findAllItemsForRequestTest() {
        List<Item> items = repository.findAllItemsForRequest(1L);
        Assertions.assertNotNull(items);
        Assertions.assertEquals(0, items.size());
    }

    @Test
    void searchTest() {
        List<Item> items = repository.search("");
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.get(0), item);
    }
}

package ru.practicum.shareit.booking;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Item item;

    private Booking lastBooking;

    private Booking nextBooking;

    private final LocalDateTime nextStart = LocalDateTime.now().plusSeconds(1);
    private final LocalDateTime nextEnd = LocalDateTime.now().plusSeconds(2);

    private final LocalDateTime currentTime = LocalDateTime.now();

    private final LocalDateTime lastStart = LocalDateTime.now().minusSeconds(2);
    private final LocalDateTime lastEnd = LocalDateTime.now().minusSeconds(1);



    @BeforeEach
    void setUp() {
        user = new User(null, "a", "b@ya.ru");
        item = new Item(null, "a", "b", true, user, null);
        lastBooking = new Booking(null, lastStart, lastEnd, item, user, BookingStatus.APPROVED.name());
        nextBooking = new Booking(null, nextStart, nextEnd, item, user, BookingStatus.WAITING.name());
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item).getId();
        Long lastBookingId = bookingRepository.save(lastBooking).getId();
        Long nextBookingId = bookingRepository.save(nextBooking).getId();
        user.setId(userId);
        item.setId(itemId);
        lastBooking.setId(lastBookingId);
        nextBooking.setId(nextBookingId);
    }

    @AfterEach
    void tearDown() {
        userRepository.delete(user);
        itemRepository.delete(item);
        bookingRepository.delete(lastBooking);
        bookingRepository.delete(nextBooking);
    }

    @Test
    void getAllByStatusTest() {
        List<Booking> bookingList = bookingRepository.findAllByStatus(BookingStatus.APPROVED.name(),
                user.getId(),
                PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(lastBooking, bookingList.get(0));
    }

    @Test
    void findAllForOwnerByStatusTest() {
        List<Booking> bookingList = bookingRepository.findAllForOwnerByStatus(BookingStatus.APPROVED.name(),
                user.getId(),
                PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(lastBooking, bookingList.get(0));
    }

    @Test
    void findAllOrderByEndTest() {
        List<Booking> bookingList = bookingRepository.findAllOrderByEnd(user.getId(), PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(2, bookingList.size());
        Assertions.assertEquals(nextBooking, bookingList.get(0));
        Assertions.assertEquals(lastBooking, bookingList.get(1));
    }

    @Test
    void findCurrentBookingsTest() {
        List<Booking> bookingList = bookingRepository.findAllByEndIsAfterAndStartIsBefore(currentTime,
                currentTime, PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(0, bookingList.size());
    }

    @Test
    void findAllByCurrentStateForOwnerTest() {
        List<Booking> bookingList = bookingRepository.findAllByCurrentStateFowOwner(currentTime,
                user.getId(), PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(0, bookingList.size());
    }

    @Test
    void findAllByPastStateTest() {
        List<Booking> bookingList = bookingRepository.findAllByEndIsBefore(currentTime,
                PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(lastBooking, bookingList.get(0));
    }

    @Test
    void findAllByFutureStateTest() {
        List<Booking> bookingList = bookingRepository.findAllByStartIsAfter(currentTime,
                PageRequest.of(0, 20));
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(nextBooking, bookingList.get(0));
    }

    @Test
    void findAllBookingsForCommentTest() {
        List<Booking> bookingList = bookingRepository.findAllBookingsForItemAndUserByEndTime(item.getId(),
                user.getId(), currentTime);
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(lastBooking, bookingList.get(0));
    }

    @Test
    void findLastBookingForItemTest() {
        List<Booking> bookingList = bookingRepository.findLastBookingForItem(currentTime, item.getId());
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(lastBooking, bookingList.get(0));
    }

    @Test
    void findNextBookingForItemTest() {
        List<Booking> bookingList = bookingRepository.findNextBookingForItem(currentTime, item.getId());
        Assertions.assertNotNull(bookingList);
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(nextBooking, bookingList.get(0));
    }
}

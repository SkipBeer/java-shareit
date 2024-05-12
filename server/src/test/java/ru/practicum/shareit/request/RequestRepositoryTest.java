package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest request;
    private User user;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = new User(null, "a", "b@ya.ru");
        request = new ItemRequest(null, "a", user, currentTime);
        Long userId = userRepository.save(user).getId();
        Long requestId = requestRepository.save(request).getId();
        user.setId(userId);
        request.setId(requestId);
    }

    @AfterEach
    void tearDown() {
        requestRepository.delete(request);
        userRepository.delete(user);
    }

    @Test
    void findByRequestorIdTest() {
        List<ItemRequest> requests = requestRepository.findByRequestorId(user.getId());
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(request, requests.get(0));
    }

    @Test
    void findAllFromOtherUsersTest() {
        List<ItemRequest> requests = requestRepository.findAllFromOtherUsers(request.getId(),
                PageRequest.of(0, 20));
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(0, requests.size());
    }
}

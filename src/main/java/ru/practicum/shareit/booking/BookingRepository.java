package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.booker.id = ?2 and b.status = ?1 order by b.end desc")
    List<Booking> findAllByStatus(String status, Long sharerId, Pageable pageable);

    @Query("select b from Booking b where b.item.user.id = ?2 and b.status = ?1 order by b.end desc")
    List<Booking> findAllForOwnerByStatus(String status, Long sharerId, Pageable pageable);

    @Query("select b from Booking b order by b.end desc")
    List<Booking> findAllOrderByEnd(Pageable pageable);

    @Query("select b from Booking b order by b.end desc")
    List<Booking> findAllOrderByEndPageable();

    List<Booking> findAllByEndIsAfterAndStartIsBefore(LocalDateTime end, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b where b.start < ?1 and b.end > ?1 and b.item.user.id = ?2")
    List<Booking> findAllByCurrentStateFowOwner(LocalDateTime currentTime, Long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.end < ?1 order by b.end desc")
    List<Booking> findAllByEndIsBefore(LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b where b.start > ?1 order by b.end desc")
    List<Booking> findAllByStartIsAfter(LocalDateTime start, Pageable pageable);


    @Query("select b from Booking b where b.item.user.id = ?1 order by b.end desc")
    List<Booking> findAllByOwner(Long ownerId, Pageable pageable);

    @Query("select b from Booking b where b.item.id = ?1")
    List<Booking> findAllBookingsForItem(Long itemId);

    @Query("select b from Booking b where b.item.id = ?1 and b.booker.id = ?2 and b.end < ?3")
    List<Booking> findAllBookingsForItemAndUserByEndTime(Long itemId, Long userId, LocalDateTime commentTime);

    @Query("select b from Booking b where b.start <= ?1 and b.item.id = ?2 order by b.end desc")
    List<Booking> findLastBookingForItem(LocalDateTime now, Long itemId);

    @Query("select b from Booking b where b.start > ?1 and b.item.id = ?2 order by b.start asc")
    List<Booking> findNextBookingForItem(LocalDateTime now, Long itemId);
}

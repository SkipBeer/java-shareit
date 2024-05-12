package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorId(Long requestorId);

    @Query("select r from ItemRequest r where r.id != ?1")
    List<ItemRequest> findAllFromOtherUsers(Long sharerId, Pageable pageable);
}

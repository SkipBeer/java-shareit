package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    //@FutureOrPresent
    private LocalDateTime start;
    //@Future
    private LocalDateTime end;
}
package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDtoResponse {
    private Long id;
    private Instant start;
    private Instant end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus state;
}

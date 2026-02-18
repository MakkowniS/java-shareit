package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDtoResponse {
    private Long id;
    private Instant start;
    private Instant end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus state;
}

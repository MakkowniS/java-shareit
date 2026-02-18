package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Instant;

@Data
public class BookingDtoRequest {

    @NotNull
    private Instant start;

    @NotNull
    private Instant end;

    @NotNull
    private Long itemId;

    private Long bookerId;
    private BookingStatus bookingStatus;

}

package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    List<BookingDtoResponse> getBookingsByState(Long userId, String state, Integer from, Integer size);

    List<BookingDtoResponse> getBookingByStateForOwner(Long userId, String state, Integer from, Integer size);

    BookingDtoResponse getBookingById(Long bookingId, Long userId);

    BookingDtoResponse saveBooking(BookingDtoRequest dtoRequest, Long userId);

    BookingDtoResponse changeBookingState(Long bookingId, Boolean approved, Long userId);
}

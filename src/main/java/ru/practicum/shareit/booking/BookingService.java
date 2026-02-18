package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    List<BookingDtoResponse> getBookingsByState(String state, Long userId);

    BookingDtoResponse getBookingById(Long bookingId);

    List<BookingDtoResponse> getBookingByStateForOwner(String state, Long userId);

    BookingDtoResponse saveBooking(@Valid BookingDtoRequest dtoRequest, Long userId);

    void changeBookingState(Long bookingId, Boolean approved);
}

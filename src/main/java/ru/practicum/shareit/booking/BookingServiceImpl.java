package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Override
    public List<BookingDtoResponse> getBookingsByState(String state, Long userId) {
        return List.of();
    }

    @Override
    public BookingDtoResponse getBookingById(Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDtoResponse> getBookingByStateForOwner(String state, Long userId) {
        return List.of();
    }

    @Override
    public BookingDtoResponse saveBooking(BookingDtoRequest dtoRequest, Long userId) {
        return null;
    }

    @Override
    public void changeBookingState(Long bookingId, Boolean approved) {

    }
}

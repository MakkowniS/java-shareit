package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.error.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public List<BookingDtoResponse> getBookingsByState(String state, Long userId) {
        return List.of();
    }

    @Override
    public BookingDtoResponse getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(BookingMapper::mapToBookingDtoResponse)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
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

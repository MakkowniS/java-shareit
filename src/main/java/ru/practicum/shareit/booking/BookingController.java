package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDtoResponse> getBookingsByState(@RequestParam(defaultValue = "ALL") String state,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByState(state, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingByStateForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByStateForOwner(state, userId);
    }

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoRequest dtoRequest,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.saveBooking(dtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeBookingState(@PathVariable("bookingId") Long bookingId,
                                                 @RequestParam(required = true) Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.changeBookingState(bookingId, approved, userId);
    }

}

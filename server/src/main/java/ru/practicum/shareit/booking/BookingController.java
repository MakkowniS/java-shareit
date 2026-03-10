package ru.practicum.shareit.booking;

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
    public List<BookingDtoResponse> getBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingsByState(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable("bookingId") Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingByStateForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingByStateForOwner(userId, state, from, size);
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingDtoRequest dtoRequest) {
        return bookingService.saveBooking(dtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeBookingState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("bookingId") Long bookingId,
                                                 @RequestParam(required = true) Boolean approved) {
        return bookingService.changeBookingState(bookingId, approved, userId);
    }

}

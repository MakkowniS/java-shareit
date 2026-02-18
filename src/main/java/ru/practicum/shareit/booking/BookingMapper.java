package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

public class BookingMapper {

    public static BookingDtoResponse mapToBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .state(booking.getState())
                .build();
    }

    public static Booking mapDtoRequestToBooking(BookingDtoRequest dtoRequest){
        Booking booking = new Booking();
        booking.setStart(dtoRequest.getStart());
        booking.setEnd(dtoRequest.getEnd());
        return booking;
    }

}

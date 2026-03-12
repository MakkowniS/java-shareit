package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDtoResponse mapToBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(LocalDateTime.ofInstant(booking.getStart(), ZoneId.systemDefault()))
                .end(LocalDateTime.ofInstant(booking.getEnd(), ZoneId.systemDefault()))
                .item(ItemMapper.mapToItemDtoShort(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getState())
                .build();
    }

    public static List<BookingDtoResponse> mapToBookingDtoResponse(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoResponse)
                .collect(Collectors.toList());
    }

    public static Booking mapDtoRequestToBooking(BookingDtoRequest dtoRequest) {
        Booking booking = new Booking();
        booking.setStart(dtoRequest.getStart().toInstant(ZoneOffset.UTC));
        booking.setEnd(dtoRequest.getEnd().toInstant(ZoneOffset.UTC));
        return booking;
    }

}

package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    void testBookingDtoResponse() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 3, 11, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 11, 12, 0);

        BookingDtoResponse dto = new BookingDtoResponse(1L, start, end, null, null, BookingStatus.WAITING);

        JsonContent<BookingDtoResponse> result = json.write(dto);

        // Проверка, что дата сериализуется в формате ISO
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-03-11T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}
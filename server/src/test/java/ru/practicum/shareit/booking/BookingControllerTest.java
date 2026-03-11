package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final String headerName = "X-Sharer-User-Id";
    private BookingDtoResponse responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new BookingDtoResponse();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.WAITING);
        responseDto.setStart(LocalDateTime.now().plusDays(1));
        responseDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_shouldReturn200() throws Exception {
        BookingDtoRequest requestDto = new BookingDtoRequest(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L);

        when(bookingService.saveBooking(any(), anyLong())).thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .header(headerName, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void changeBookingState_shouldUpdateStatus() throws Exception {
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.changeBookingState(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(headerName, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(responseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(headerName, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBookingsByState_shouldUseDefaultParams() throws Exception {
        when(bookingService.getBookingsByState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings")
                        .header(headerName, 1L)) // должны сработать дефолты
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingByStateForOwner_shouldReturnList() throws Exception {
        when(bookingService.getBookingByStateForOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header(headerName, 1L)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
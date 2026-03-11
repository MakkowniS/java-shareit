package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.error.WrongRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@mail.ru");
        item = new Item(1L, "Drill", "Powerful", true, user, null);
        booking = new Booking(1L, Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(2)),
                item, user, BookingStatus.WAITING);
    }

    @Test
    void saveBooking_whenItemNotAvailable_shouldThrowWrongRequestException() {
        item.setAvailable(false);
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findByIdOrThrow(anyLong())).thenReturn(item);

        BookingDtoRequest request = new BookingDtoRequest(1L, LocalDateTime.now(), LocalDateTime.now(), user.getId());

        assertThrows(WrongRequestException.class, () -> bookingService.saveBooking(request, 1L));
    }

    @Test
    void saveBooking_whenOverlapping_shouldThrowWrongRequestException() {
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(user);
        when(itemRepository.findByIdOrThrow(anyLong())).thenReturn(item);
        // Имитируем, что пересечение найдено
        when(bookingRepository.existsOverlapping(anyLong(), any(), any())).thenReturn(true);

        BookingDtoRequest request = new BookingDtoRequest(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), user.getId());

        assertThrows(WrongRequestException.class, () -> bookingService.saveBooking(request, 1L));
    }

    @Test
    void changeBookingState_whenNotOwner_shouldThrowSecurityException() {
        User notOwner = new User(2L, "NotOwner", "other@mail.ru");
        booking.getItem().setOwner(notOwner); // Владелец — User 2

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Пытается подтвердить юзер 1
        assertThrows(SecurityException.class, () ->
                bookingService.changeBookingState(1L, true, 1L)
        );
    }

    @Test
    void changeBookingState_whenAlreadyApproved_shouldThrowWrongRequestException() {
        booking.setState(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(WrongRequestException.class, () ->
                bookingService.changeBookingState(1L, true, 1L)
        );
    }
}
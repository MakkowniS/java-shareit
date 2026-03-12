package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.WrongRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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
    void getBookingByStateForOwner_whenValid_shouldReturnList() {
        Long ownerId = 1L;
        String state = "ALL";

        // Создаем пользователя и вещь для бронирования (чтобы маппер не упал на вложенных объектах)
        User user = new User(1L, "User", "user@mail.ru");
        Item item = new Item(1L, "Item", "Desc", true, user, null);

        // Создаем объект бронирования с заполненными датами
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.now().plus(Duration.ofDays(1)));
        booking.setEnd(Instant.now().plus(Duration.ofDays(2)));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setState(BookingStatus.WAITING);

        when(userRepository.findByIdOrThrow(ownerId)).thenReturn(user);
        when(itemRepository.existsByOwner_Id(ownerId)).thenReturn(true);

        // Возвращаем список с подготовленным объектом вместо "пустого" new Booking()
        when(bookingRepository.findOwnerBooking(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> result = bookingService.getBookingByStateForOwner(ownerId, state, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId()); // Проверяем, что маппинг прошел успешно
        verify(bookingRepository).findOwnerBooking(eq(ownerId), eq(state), any(Pageable.class));
    }

    @Test
    void getBookingByStateForOwner_whenUserHasNoItems_shouldThrowNotFoundException() {
        Long ownerId = 1L;
        when(userRepository.findByIdOrThrow(ownerId)).thenReturn(new User());
        when(itemRepository.existsByOwner_Id(ownerId)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingByStateForOwner(ownerId, "ALL", 0, 10)
        );

        assertEquals("Пользователь не владеет ни одной вещью", ex.getMessage());
        verify(bookingRepository, never()).findOwnerBooking(anyLong(), anyString(), any());
    }

    @Test
    void getBookingByStateForOwner_whenUserNotFound_shouldThrowException() {
        when(userRepository.findByIdOrThrow(anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingByStateForOwner(99L, "ALL", 0, 10)
        );
    }

    @Test
    void getBookingById_whenUserIsBooker_shouldReturnBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(bookingRepository.existsByIdAndBookerId(bookingId, userId)).thenReturn(true);
        when(itemRepository.existsByIdAndOwner_Id(anyLong(), eq(userId))).thenReturn(false);

        BookingDtoResponse result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_whenUserIsOwner_shouldReturnBooking() {
        Long userId = 2L;
        Long bookingId = 1L;
        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(bookingRepository.existsByIdAndBookerId(bookingId, userId)).thenReturn(false);
        when(itemRepository.existsByIdAndOwner_Id(anyLong(), eq(userId))).thenReturn(true);

        BookingDtoResponse result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
        verify(itemRepository).existsByIdAndOwner_Id(anyLong(), eq(userId));
    }

    @Test
    void getBookingById_whenUserHasNoAccess_shouldThrowWrongRequestException() {
        Long userId = 3L;
        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        when(bookingRepository.existsByIdAndBookerId(anyLong(), eq(userId))).thenReturn(false);
        when(itemRepository.existsByIdAndOwner_Id(anyLong(), eq(userId))).thenReturn(false);

        assertThrows(WrongRequestException.class, () ->
                bookingService.getBookingById(1L, userId)
        );
    }

    @Test
    void getBookingById_whenBookingNotFound_shouldThrowNotFoundException() {
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(new User());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(99L, 1L)
        );
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
    void changeBookingState_whenApproved_shouldSetStatusApproved() {
        Long userId = 1L;
        // Убеждаемся, что владелец совпадает с userId, а статус WAITING
        booking.getItem().getOwner().setId(userId);
        booking.setState(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        BookingDtoResponse result = bookingService.changeBookingState(1L, true, userId);

        // Assert
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void changeBookingState_whenRejected_shouldSetStatusRejected() {
        Long userId = 1L;
        booking.getItem().getOwner().setId(userId);
        booking.setState(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        // Передаем approved = false
        BookingDtoResponse result = bookingService.changeBookingState(1L, false, userId);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void changeBookingState_whenBookingNotFound_shouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.changeBookingState(99L, true, 1L)
        );
    }

    @Test
    void changeBookingState_whenNotOwner_shouldThrowSecurityException() {
        User notOwner = new User(2L, "NotOwner", "other@mail.ru");
        booking.getItem().setOwner(notOwner); // Владелец — User 2

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

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
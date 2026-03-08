package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.WrongRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<BookingDtoResponse> getBookingsByState(String state, Long userId) {
        userRepository.findByIdOrThrow(userId);

        BookingState bookingState = checkBookingState(state);

        List<Booking> bookingsList = bookingRepository.findCurrentUserBooking(userId, bookingState.name());

        return BookingMapper.mapToBookingDtoResponse(bookingsList);
    }

    @Override
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        // Проверка на существование пользователя
        userRepository.findByIdOrThrow(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // Проверка на автора бронирования или хозяина вещи
        boolean isBooker = bookingRepository.existsByIdAndBookerId(booking.getId(), userId);
        boolean isOwner = itemRepository.existsByIdAndOwner_Id(booking.getItem().getId(), userId);
        // Если не автор и не хозяин, то бросаем исключение
        if (!isBooker && !isOwner) {
            throw new WrongRequestException("У вас нет прав на просмотр этого бронирования");
        }

        return BookingMapper.mapToBookingDtoResponse(booking);

    }

    @Override
    public List<BookingDtoResponse> getBookingByStateForOwner(String state, Long ownerId) {

        // Проверка User
        userRepository.findByIdOrThrow(ownerId);

        // Проверка на правильный статус
        BookingState bookingState = checkBookingState(state);

        // Проверка на владение вещами
        if (!itemRepository.existsByOwner_Id(ownerId)) {
            throw new NotFoundException("Пользователь не владеет ни одной вещью");
        }

        List<Booking> bookingsList = bookingRepository.findOwnerBooking(ownerId, bookingState.name());
        return BookingMapper.mapToBookingDtoResponse(bookingsList);
    }

    @Override
    @Transactional
    public BookingDtoResponse saveBooking(BookingDtoRequest dtoRequest, Long userId) {
        User user = userRepository.findByIdOrThrow(userId);
        Item item = itemRepository.findByIdOrThrow(dtoRequest.getItemId());
        if (!item.isAvailable()) {
            throw new WrongRequestException("Эта вещь недоступна для бронирования");
        }
        Booking booking = BookingMapper.mapDtoRequestToBooking(dtoRequest);

        // Проверка на пересечение во времени
        boolean isOverlapping = bookingRepository.existsOverlapping(
                item.getId(), booking.getStart(), booking.getEnd());
        if (isOverlapping) {
            throw new WrongRequestException("На указанное время вещь недоступна для бронирования");
        }

        booking.setItem(item);
        booking.setBooker(user);
        booking.setState(BookingStatus.WAITING);

        return BookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse changeBookingState(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new SecurityException("У вас нет доступа на подтверждение этой брони");
        }
        userRepository.findByIdOrThrow(userId);
        if (booking.getState() != BookingStatus.WAITING) {
            throw new WrongRequestException("Невозможно изменить статус этой брони");
        }

        if (approved) {
            booking.setState(BookingStatus.APPROVED);
        } else {
            booking.setState(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
    }

    private BookingState checkBookingState(String state) {
        return BookingState.from(state)
                .orElseThrow(() -> new WrongRequestException("Неизвестное состояние" + state));
    }

}

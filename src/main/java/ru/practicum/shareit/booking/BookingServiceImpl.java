package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<BookingDtoResponse> getBookingsByState(String state, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));

        List<Booking> bookingsList = bookingRepository.findCurrentUserBooking(userId, state);

        return BookingMapper.mapToBookingDtoResponse(bookingsList);
    }

    @Override
    public BookingDtoResponse getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(BookingMapper::mapToBookingDtoResponse)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public List<BookingDtoResponse> getBookingByStateForOwner(String state, Long ownerId) {

        // Проверка User
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));

        // Проверка на владение вещами
        if (!itemRepository.existsByOwner_Id(ownerId)) {
            throw new NotFoundException("Пользователь не владеет ни одной вещью");
        }

        List<Booking> bookingsList = bookingRepository.findOwnerBooking(ownerId, state);
        return BookingMapper.mapToBookingDtoResponse(bookingsList);
    }

    @Override
    @Transactional
    public BookingDtoResponse saveBooking(BookingDtoRequest dtoRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));
        Item item = itemRepository.findById(dtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        Booking booking = BookingMapper.mapDtoRequestToBooking(dtoRequest);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setState(BookingStatus.WAITING);

        return BookingMapper.mapToBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void changeBookingState(Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (approved) {
            booking.setState(BookingStatus.APPROVED);
        } else {
            booking.setState(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
    }
}

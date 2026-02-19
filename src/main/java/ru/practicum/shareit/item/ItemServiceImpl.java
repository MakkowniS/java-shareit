package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoResponseWithBooking> getItems(Long userId) {
        checkUserExists(userId);
        List<Item> items = itemRepository.findByOwner_Id(userId);
        return fillDtoWithBookingsAndComments(items, userId);
    }

    @Override
    public ItemDtoResponseWithBooking getItemById(Long itemId, Long userId) {
        checkUserExists(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return fillDtoWithBookingsAndComments(List.of(item), userId).getFirst();
    }

    @Override
    public List<ItemDtoResponseShort> searchItemToRent(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchItemsToRent(text).stream()
                .map(ItemMapper::mapToItemDtoShort)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDtoResponseShort saveItem(ItemDtoRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));
        Item newItem = ItemMapper.mapDtoRequestToItem(dto);
        newItem.setOwner(user);
        return ItemMapper.mapToItemDtoShort(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public CommentDtoResponse saveComment(CommentDtoRequest dtoRequest, Long userId, Long itemId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndState(
                userId, itemId, Instant.now(), BookingStatus.APPROVED);
        if (!hasBooking) {
            throw new NotFoundException("Аренда не завершена или вы не арендовали данную вещь");
        }

        Comment comment = CommentMapper.mapDtoToComment(dtoRequest, author, item);
        comment = commentRepository.save(comment);

        return CommentMapper.mapCommentToDtoResponse(comment);
    }

    @Override
    @Transactional
    public ItemDtoResponseShort editItem(ItemDtoRequest dto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new SecurityException("У вас нет доступа к редактированию данной вещи");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return ItemMapper.mapToItemDtoShort(itemRepository.save(item));
    }

    // Вспомогательный метод для getItemById
    private List<ItemDtoResponseWithBooking> fillDtoWithBookingsAndComments(List<Item> items, Long userId) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Instant now = Instant.now();

        // Получаем все бронирования и группируем по ID
        Map<Long, List<Booking>> bookingsMap = bookingRepository.findByItemIdInAndStateNot(itemIds, BookingStatus.REJECTED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        // Получаем все комменты и группируем по ID
        Map<Long, List<Comment>> commentsMap = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemDtoResponseWithBooking dto = ItemMapper.mapToItemDtoWithBooking(item);

                    // Получаем списки комментов для Item, либо пустые
                    List<Comment> comments = commentsMap.getOrDefault(item.getId(), List.of());
                    dto.setComments((comments.stream()
                            .map(CommentMapper::mapCommentToDtoResponse)
                            .collect(Collectors.toList())));

                    // Если владелец, то получаем список бронирований и выставляем Last и Next
                    if (item.getOwner().getId().equals(userId)) {
                        List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), List.of());
                        dto.setLastBooking(findLastBooking(itemBookings, now));
                        dto.setNextBooking(findNextBooking(itemBookings, now));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private BookingDtoResponse findLastBooking(List<Booking> bookings, Instant now) {
        return bookings.stream()
                .filter(b -> !b.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::mapToBookingDtoResponse)
                .orElse(null);
    }

    private BookingDtoResponse findNextBooking(List<Booking> bookings, Instant now) {
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::mapToBookingDtoResponse)
                .orElse(null);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDtoResponseWithBooking> getItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRepository.findByOwner_Id(userId).stream()
                .map(item -> createDtoResponseWithBooking(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponseWithBooking getItemById(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return createDtoResponseWithBooking(item, userId);
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
    public ItemDtoResponseShort saveItem(ItemDtoRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));
        Item newItem = ItemMapper.mapDtoRequestToItem(dto);
        newItem.setOwner(user);
        return ItemMapper.mapToItemDtoShort(itemRepository.save(newItem));
    }

    @Override
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

    private ItemDtoResponseWithBooking createDtoResponseWithBooking(Item item, Long userId){
        ItemDtoResponseWithBooking dtoResponseWithBooking = ItemMapper.mapToItemDtoWithBooking(item);

        if (item.getOwner().getId().equals(userId)) {
            Instant now = Instant.now();

            // Последнее бронирование (бывшее или идущее)
            Booking lastBooking = bookingRepository
                    .findByItemIdAndStartBeforeOrderByEndDesc(item.getId(), now)
                    .stream()
                    .filter(b -> !b.getState().equals(BookingStatus.REJECTED))
                    .findFirst()
                    .orElse(null);

            Booking nextBooking = bookingRepository
                    .findByItemIdAndStartAfterOrderByStartAsc(item.getId(), now)
                    .stream()
                    .filter(b -> !b.getState().equals(BookingStatus.REJECTED))
                    .findFirst()
                    .orElse(null);

            if (lastBooking != null) {
                dtoResponseWithBooking.setLastBooking(BookingMapper.mapToBookingDtoResponse(lastBooking));
            }
            if (nextBooking != null) {
                dtoResponseWithBooking.setNextBooking(BookingMapper.mapToBookingDtoResponse(nextBooking));
            }
        }
        return dtoResponseWithBooking;
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private static final HashMap<Long, Item> ITEMS = new HashMap<>();

    @Override
    public List<Item> getItems(Long userId) {
        return ITEMS.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(ITEMS.get(itemId));
    }

    @Override
    public List<Item> searchItemToRent(String text) {
        return ITEMS.values().stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(text)
                        || item.getDescription() != null && item.getDescription().toLowerCase().contains(text))
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(ItemDto itemDto, User user) {
        Long id = getNextId();
        Item newItem = Item.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
        ITEMS.put(id, newItem);
        return newItem;
    }

    @Override
    public Item editItem(ItemDto itemDto, Long itemId) {
        Item storedItem = ITEMS.get(itemId);
        if (itemDto.getName() != null) {
            storedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            storedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            storedItem.setAvailable(itemDto.getAvailable());
        }
        return storedItem;
    }

    private Long getNextId() {
        long currentId = ITEMS.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentId;
    }

}

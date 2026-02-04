package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> searchItemToRent(String request);

    ItemDto saveItem(ItemDto item, Long userId);

    ItemDto editItem(ItemDto item, Long itemId, Long userId);
}

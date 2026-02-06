package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.List;

public interface ItemService {
    List<ItemDtoResponse> getItems(Long userId);

    ItemDtoResponse getItemById(Long itemId);

    List<ItemDtoResponse> searchItemToRent(String request);

    ItemDtoResponse saveItem(ItemDtoRequest item, Long userId);

    ItemDtoResponse editItem(ItemDtoRequest item, Long itemId, Long userId);
}

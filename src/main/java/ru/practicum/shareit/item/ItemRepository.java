package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getItems(Long userId);

    Optional<Item> getItemById(Long itemId);

    List<Item> searchItemToRent(String request);

    Item saveItem(ItemDto item, User user);

    Item editItem(ItemDto item, Long itemId);
}

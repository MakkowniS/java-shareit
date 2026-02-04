package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    public List<Item> getItems(Long userId);

    public Optional<Item> getItemById(Long itemId);

    public List<Item> searchItemToRent(String request);

    public Item saveItem(ItemDto item, User user);

    public Item editItem(ItemDto item, Long itemId);
}

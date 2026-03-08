package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoResponseWithBooking> getItems(Long userId);

    ItemDtoResponseWithBooking getItemById(Long itemId, Long userId);

    List<ItemDtoResponseShort> searchItemToRent(String request);

    ItemDtoResponseShort saveItem(ItemDtoRequest item, Long userId);

    CommentDtoResponse saveComment(CommentDtoRequest dtoRequest, Long userId, Long itemId);

    ItemDtoResponseShort editItem(ItemDtoRequest item, Long itemId, Long userId);
}

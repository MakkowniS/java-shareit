package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDtoResponseShort mapToItemDtoShort(Item item) {
        return ItemDtoResponseShort.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item mapDtoRequestToItem(ItemDtoRequest dtoRequest) {
        Item item = new Item();
        item.setName(dtoRequest.getName());
        item.setDescription(dtoRequest.getDescription());
        item.setAvailable(dtoRequest.getAvailable());
        return item;
    }

    public static ItemDtoResponseWithBooking mapToItemDtoWithBooking(Item item) {
        return ItemDtoResponseWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

}

package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDtoResponse mapToItemDto(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item mapDtoRequestToItem(ItemDtoRequest dtoRequest) {
        Item item = new Item();
        item.setName(dtoRequest.getName());
        item.setDescription(dtoRequest.getDescription());
        item.setAvailable(dtoRequest.getAvailable());
        return item;
    }

}

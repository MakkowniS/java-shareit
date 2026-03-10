package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest mapRequestToItemRequest(ItemRequestRequestDto itemRequestDtoRequest) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequest(itemRequestDtoRequest.getDescription());
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getRequest())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestWithAnswersDto mapToItemRequestWithAnswers(ItemRequest itemRequest, List<Item> items) {
        return ItemRequestWithAnswersDto.builder()
                .id(itemRequest.getId())
                .request(itemRequest.getRequest())
                .created(itemRequest.getCreated())
                .items(items.stream()
                        .map(item -> ItemRequestAnswerDto.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .ownerId(item.getOwner().getId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

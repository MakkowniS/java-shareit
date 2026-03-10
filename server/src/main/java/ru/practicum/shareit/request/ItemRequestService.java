package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(ItemRequestRequestDto dtoRequest, Long userId);

    ItemRequestDto getItemRequestById(Long id, Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    List<ItemRequestWithAnswersDto> getAllItemRequestsWithAnswers(Long userId);
}

package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {
    public ItemRequestDto saveItemRequest(ItemRequestRequestDto dtoRequest, Long userId);

    public ItemRequestDto getItemRequestById(Long id, Long userId);

    public List<ItemRequestDto> getAllItemRequests(Long userId);

    public List<ItemRequestWithAnswersDto> getAllItemRequestsWithAnswers(Long userId);
}

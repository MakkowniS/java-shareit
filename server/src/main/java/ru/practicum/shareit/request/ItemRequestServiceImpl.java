package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        userRepository.findByIdOrThrow(userId);
        List<ItemRequest> itemRequestsList = itemRequestRepository.getAllByOrderByCreatedDesc();
        return itemRequestsList.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAllItemRequestsWithAnswers(Long userId) {
        userRepository.findByIdOrThrow(userId);

        // Получаем все запросы созданные пользователем
        List<ItemRequest> itemRequestsList = itemRequestRepository.getAllByRequestorIdOrderByCreatedDesc(userId);

        // Получаем список ID запросов
        List<Long> requestsIds = itemRequestsList.stream()
                .map(ItemRequest::getId)
                .toList();

        // Получаем список Items отсортированный по ID Запроса
        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestsIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        // Собираем список и мапим в RequestDto присущие Items
        return itemRequestsList.stream()
                .map(request -> ItemRequestMapper.mapToItemRequestWithAnswers(request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestById(Long id, Long userId) {
        userRepository.findByIdOrThrow(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запроса с таким ID не найдено"));

        List<Item> requestsItems = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        return ItemRequestMapper.mapToItemRequestWithAnswers(itemRequest, requestsItems);
    }

    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(ItemRequestRequestDto dtoRequest, Long userId) {
        User requestor = userRepository.findByIdOrThrow(userId);
        ItemRequest newItemRequest = ItemRequestMapper.mapRequestToItemRequest(dtoRequest);
        newItemRequest.setRequestor(requestor);

        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(newItemRequest));
    }

}

package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> getAllByOrderByCreatedDesc();

    List<ItemRequest> getAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    default ItemRequest findItemRequestByIdOrThrow(Long itemRequestId){
        return findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID: " + itemRequestId + " не найден"));
    }
}

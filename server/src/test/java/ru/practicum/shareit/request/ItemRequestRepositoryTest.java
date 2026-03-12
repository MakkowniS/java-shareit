package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestRepositoryTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findItemRequestByIdOrThrow_whenExists_shouldReturnRequest() {
        Long requestId = 1L;
        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(itemRequestRepository.findItemRequestByIdOrThrow(requestId)).thenCallRealMethod();

        ItemRequest result = itemRequestRepository.findItemRequestByIdOrThrow(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void findItemRequestByIdOrThrow_whenNotFound_shouldThrowNotFoundException() {
        Long requestId = 99L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        when(itemRequestRepository.findItemRequestByIdOrThrow(requestId)).thenCallRealMethod();

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                itemRequestRepository.findItemRequestByIdOrThrow(requestId)
        );

        assertTrue(ex.getMessage().contains("Запрос с ID: " + requestId + " не найден"));
    }
}
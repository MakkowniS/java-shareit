package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@mail.ru");
        itemRequest = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
    }

    @Test
    void saveItemRequest_shouldSaveSuccessfully() {
        ItemRequestRequestDto dto = new ItemRequestRequestDto("Need a drill");
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(user);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.saveItemRequest(dto, 1L);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        verify(itemRequestRepository).save(any());
    }

    @Test
    void getItemRequestById_whenFound_shouldReturnDtoWithItems() {
        Long userId = 1L;
        Long requestId = 10L;

        User user = new User(userId, "User", "user@mail.ru");
        ItemRequest request = new ItemRequest(requestId, "Need a drill", user, LocalDateTime.now());

        Item item = new Item(1L, "Drill", "Powerful", true, new User(), request);

        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));

        ItemRequestWithAnswersDto result = itemRequestService.getItemRequestById(requestId, userId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertFalse(result.getItems().isEmpty());
        assertEquals("Drill", result.getItems().getFirst().getName());

        verify(userRepository).findByIdOrThrow(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findAllByRequestIdIn(anyList());
    }

    @Test
    void getItemRequestById_whenRequestNotFound_shouldThrowNotFoundException() {
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(99L, 1L)
        );
    }

    @Test
    void getAllItemRequestsWithAnswers_shouldGroupItemsCorrectly() {
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(user);
        when(itemRequestRepository.getAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        Item itemForRequest = new Item(1L, "Drill", "Desc", true, new User(), itemRequest);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(itemForRequest));

        List<ItemRequestWithAnswersDto> result = itemRequestService.getAllItemRequestsWithAnswers(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getFirst().getItems().size());
        assertEquals("Drill", result.getFirst().getItems().getFirst().getName());
    }
}
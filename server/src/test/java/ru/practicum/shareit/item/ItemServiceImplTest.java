package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.error.WrongRequestException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
    }

    @Test
    void editItem_whenUserIsNotOwner_shouldThrowException() throws Exception {
        Long otherUserId = 2L;
        ItemDtoRequest updateDto = new ItemDtoRequest("New Name", null, null, null);

        when(itemRepository.findByIdOrThrow(1L)).thenReturn(item);

        assertThrows(SecurityException.class, () -> itemService.editItem(updateDto, 1L, otherUserId));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void saveComment_whenNoFinishedBooking_shouldThrowWrongRequestException() {
        Long userId = 3L;
        Long itemId = 1L;

        when(userRepository.findByIdOrThrow(userId)).thenReturn(new User());
        when(itemRepository.findByIdOrThrow(itemId)).thenReturn(item);
        // Имитируем, что завершенных бронирований нет
        when(bookingRepository.existsFinishedBooking(eq(userId), eq(itemId), any()))
                .thenReturn(false);

        assertThrows(WrongRequestException.class, () ->
                itemService.saveComment(null, userId, itemId)
        );
    }

    @Test
    void saveComment_shouldSaveAndReturnComment() {
        Comment comment = new Comment(1L, "Lorem", item, owner, LocalDateTime.now());

        when(itemRepository.findByIdOrThrow(1L)).thenReturn(item);
        when(userRepository.findByIdOrThrow(1L)).thenReturn(owner);
        when(bookingRepository.existsFinishedBooking(eq(1L), eq(item.getId()), any())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoRequest request = new CommentDtoRequest("Lorem");

        CommentDtoResponse response = itemService.saveComment(request, owner.getId(), item.getId());

        assertNotNull(response);
        assertEquals(response.getText(), "Lorem");
        assertEquals(response.getAuthorName(), owner.getName());

        verify(commentRepository).save(any(Comment.class));

    }

    @Test
    void searchItemToRent_whenTextIsEmpty_shouldReturnEmptyList() {
        List<ItemDtoResponseShort> result = itemService.searchItemToRent("");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchItemsToRent(anyString());
    }

    @Test
    void saveItem_whenValid_shouldSaveAndReturnDto() {
        ItemDtoRequest request = new ItemDtoRequest("Drill", "Powerful", true, null);
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDtoResponseShort result = itemService.saveItem(request, 1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItemById_whenUserIsOwner_shouldIncludeBookings() {
        // Настраиваем вещь и владельца
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(owner);
        when(itemRepository.findByIdOrThrow(anyLong())).thenReturn(item);

        // Настраиваем пустые списки для бронирований и комментов
        when(bookingRepository.findByItemIdInAndStateNot(anyList(), any())).thenReturn(List.of());
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(List.of());

        ItemDtoResponseWithBooking result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());

        verify(bookingRepository).findByItemIdInAndStateNot(anyList(), any());
    }

    @Test
    void searchItemToRent_whenTextValid_shouldReturnList() {
        when(itemRepository.searchItemsToRent("drill")).thenReturn(List.of(item));

        List<ItemDtoResponseShort> result = itemService.searchItemToRent("drill");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Drill", result.getFirst().getName());
    }

    @Test
    void editItem_whenUserIsOwner_shouldUpdateFields() {
        ItemDtoRequest updateDto = new ItemDtoRequest("New Drill", "New Desc", false, null);
        when(itemRepository.findByIdOrThrow(1L)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDtoResponseShort result = itemService.editItem(updateDto, 1L, 1L);

        assertEquals("New Drill", result.getName());
        assertEquals("New Desc", result.getDescription());
        assertFalse(result.getAvailable());
    }

}

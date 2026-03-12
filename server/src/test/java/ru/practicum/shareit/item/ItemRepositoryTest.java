package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryTest {

    @Mock
    private ItemRepository itemRepository;

    @Test
    void findByIdOrThrow_whenItemExists_shouldReturnItem() {
        // Arrange
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);

        // Настраиваем базу (findById), а для нашего метода просим выполнить "настоящий" код
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.findByIdOrThrow(itemId)).thenCallRealMethod();

        // Act
        Item result = itemRepository.findByIdOrThrow(itemId);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void findByIdOrThrow_whenItemNotFound_shouldThrowNotFoundException() {
        // Arrange
        Long itemId = 99L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(itemRepository.findByIdOrThrow(itemId)).thenCallRealMethod();

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                itemRepository.findByIdOrThrow(itemId)
        );

        // Проверяем, что сообщение соответствует тому, что написано в default методе
        assertEquals("Вещь с id:" + itemId + " не найдена", ex.getMessage());
    }
}
package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItems(Long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRepository.getItems(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemRepository.getItemById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public List<ItemDto> searchItemToRent(String request) {
        if (request.isBlank() || request == null) {
            return List.of();
        }
        String text = request.toLowerCase();

        return itemRepository.searchItemToRent(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto saveItem(ItemDto item, Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Указанного пользователя не существует"));
        return itemMapper.toItemDto(itemRepository.saveItem(item, user));
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new SecurityException("У вас нет доступа к редактированию данной вещи");
        }

        return itemMapper.toItemDto(itemRepository.editItem(itemDto, itemId));
    }
}

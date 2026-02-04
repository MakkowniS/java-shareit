package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemToRent(@RequestParam String text) {
        return itemService.searchItemToRent(text);
    }

    @PostMapping
    public ItemDto saveItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.saveItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestBody ItemDto item, @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.editItem(item, itemId, userId);
    }
}
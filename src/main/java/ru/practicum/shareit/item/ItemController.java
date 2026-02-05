package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoResponse> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItemToRent(@RequestParam String text) {
        return itemService.searchItemToRent(text);
    }

    @PostMapping
    public ItemDtoResponse saveItem(@Valid @RequestBody ItemDtoRequest item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.saveItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse editItem(@RequestBody ItemDtoRequest item, @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.editItem(item, itemId, userId);
    }
}
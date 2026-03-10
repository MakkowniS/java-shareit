package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoResponseWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponseWithBooking getItemById(@PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponseShort> searchItemToRent(@RequestParam String text) {
        return itemService.searchItemToRent(text);
    }

    @PostMapping
    public ItemDtoResponseShort saveItem(@RequestBody ItemDtoRequest item,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.saveItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody CommentDtoRequest comment) {
        return itemService.saveComment(comment, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponseShort editItem(@RequestBody ItemDtoRequest item,
                                         @PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.editItem(item, itemId, userId);
    }
}
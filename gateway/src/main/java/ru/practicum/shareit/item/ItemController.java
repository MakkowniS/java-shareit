package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemToRent(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @NotBlank String text) {
        return itemClient.searchItemToRent(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDtoRequest dtoRequest) {
        return itemClient.saveItem(userId, dtoRequest);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDtoRequest dtoRequest) {
        return itemClient.saveComment(userId, itemId, dtoRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Positive @PathVariable Long itemId,
                                           @RequestBody ItemDtoRequest dtoRequest) {
        return itemClient.editItem(userId, itemId, dtoRequest);
    }
}
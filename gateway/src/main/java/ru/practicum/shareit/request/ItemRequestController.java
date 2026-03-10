package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @GetMapping
    public List<ItemRequestDto> getItemRequestsWithItems(@RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable String requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {

    }

    @PostMapping
    public ItemRequestDto createItem(@RequestBody ItemRequestRequestDto dtoRequest,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {

    }



}

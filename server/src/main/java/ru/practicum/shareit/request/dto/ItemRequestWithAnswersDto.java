package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestWithAnswersDto {
    private Long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
    List<ItemRequestAnswerDto> items;
}

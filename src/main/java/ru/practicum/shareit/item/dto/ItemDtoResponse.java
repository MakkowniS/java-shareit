package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoResponse {

    Long id;
    String name;
    String description;
    Boolean available;
    Long request;

}


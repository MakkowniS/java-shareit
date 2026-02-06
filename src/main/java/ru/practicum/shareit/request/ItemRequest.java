package ru.practicum.shareit.request;

/**
 * TODO Sprint add-item-requests.
 */
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}


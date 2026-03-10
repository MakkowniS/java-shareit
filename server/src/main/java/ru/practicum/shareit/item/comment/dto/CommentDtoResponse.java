package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentDtoResponse {
    private Long id;
    private String text;
    private String authorName;
    private Instant created;
}

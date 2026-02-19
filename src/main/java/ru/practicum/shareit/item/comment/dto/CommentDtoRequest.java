package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDtoRequest {

    @NotBlank
    private String text;

}

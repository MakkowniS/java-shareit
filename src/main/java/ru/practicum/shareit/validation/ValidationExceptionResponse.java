package ru.practicum.shareit.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationExceptionResponse {

    private final List<ValidationViolation> violations;

}

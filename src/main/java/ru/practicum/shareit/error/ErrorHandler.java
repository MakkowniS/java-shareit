package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.validation.ValidationExceptionResponse;
import ru.practicum.shareit.validation.ValidationViolation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice({
        "ru.practicum.shareit.user",
        "ru.practicum.shareit.item"
})
public class ErrorHandler {

    // Обработка исключения NotFoundException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse("Не найдено.", e.getMessage());
    }

    // Обработка SecurityException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(SecurityException e) {
        return new ErrorResponse("Не найдено.", e.getMessage());
    }

    // Обработка исключения валидации MethodArgumentNotValidException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ValidationViolation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationViolation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationExceptionResponse(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse onConstraintViolationException(ConstraintViolationException e) {
        final List<ValidationViolation> violations = e.getConstraintViolations().stream()
                .map(violation -> new ValidationViolation(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());
        return new ValidationExceptionResponse(violations);
    }

}


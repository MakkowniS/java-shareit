package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.validation.ValidationExceptionResponse;
import ru.practicum.shareit.error.validation.ValidationViolation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    // Обработка исключения валидации MethodArgumentNotValidException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ValidationViolation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationViolation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationExceptionResponse(violations);
    }

    // Ошибки параметров запроса (например, @Positive на @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionResponse onConstraintViolationException(ConstraintViolationException e) {
        final List<ValidationViolation> violations = e.getConstraintViolations().stream()
                .map(violation -> new ValidationViolation(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());
        return new ValidationExceptionResponse(violations);
    }

    // Специфическая ошибка для статуса бронирования
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Недопустимый аргумент: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    // Общий перехватчик
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка: ", e);
        return new ErrorResponse("Произошла внутренняя ошибка сервера.");
    }
}

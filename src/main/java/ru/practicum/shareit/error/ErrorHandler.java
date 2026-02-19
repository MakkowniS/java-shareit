package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.validation.ValidationExceptionResponse;
import ru.practicum.shareit.validation.ValidationViolation;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    // Обработка WrongRequest
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongRequest(WrongRequestException e){
        return new ErrorResponse("Отказ.", e.getMessage());
    }

    // Обработка DuplicateDataException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateData(DuplicateDataException e) {
        return new ErrorResponse("Недоступно.", e.getMessage());
    }

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


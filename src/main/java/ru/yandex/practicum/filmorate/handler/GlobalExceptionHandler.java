package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.warn("Некорректное значение параметра {}: {}", e.getParameter(), e.getMessage());
        return new ErrorResponse("Ошибка 400", "Некорректные данные от пользователя");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final NotFoundException e) {
        log.warn("Попытка обращения к несуществующему ресурсу: {}", e.getMessage());
        return new ErrorResponse("Ошибка 404", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(final ConstraintViolationException e) {
        log.warn("Неудачная попытка валидации данных: {}", e.getConstraintViolations());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateLoginException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateEmail(final RuntimeException e) {
        log.warn("Дублирование электронной почты или логина: {}", e.getMessage());
        return new ErrorResponse("Ошибка дубликата", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Возникла непредвиденная ошибка: {}", e.getMessage());
        return new ErrorResponse("Ошибка обращения к сервису", e.getMessage());
    }
}

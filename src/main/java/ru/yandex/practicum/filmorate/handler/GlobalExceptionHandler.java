package ru.yandex.practicum.filmorate.handler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValid(final ParameterNotValidException e) {
        log.warn("Неверное значение параметра {} в строке запроса", e.getParameter());
        return new ErrorResponse(
                "Ошибка параметра строки запроса",
                String.format("Некорректное значение параметра %s: %s",
                        e.getParameter(), e.getReason())
        );
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final RuntimeException e) {
        log.warn("Попытка обращения к несуществующему ресурсу: {}", e.getMessage());
        return new ErrorResponse("Ошибка 404", e.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateLoginException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateEmail(final RuntimeException e) {
        log.warn("Неудачная попытка валидации данных: {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Возникла непредвиденная ошибка: {}", e.getMessage());
        return new ErrorResponse("Ошибка обращения к сервису", e.getMessage());
    }
}

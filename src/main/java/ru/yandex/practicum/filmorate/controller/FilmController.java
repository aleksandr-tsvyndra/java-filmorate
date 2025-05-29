package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.controller.marker.Marker;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTH_DATE;

    static {
        CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);
    }

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен http-запрос на получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен http-запрос на добавление фильма");
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(CINEMA_BIRTH_DATE)) {
                log.warn("Дата релиза {} не соответствует критерию валидации", film.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм с id {} был добавлен в базу данных", film.getId());
        return film;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен http-запрос на обновление фильма");
        if (films.containsKey(newFilm.getId())) {
            log.info("Фильм с id {} был найден в базе данных", newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            updateFilmFields(oldFilm, newFilm);
            return oldFilm;
        }
        log.warn("Фильма с id {} нет в базе данных", newFilm.getId());
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void updateFilmFields(Film oldFilm, Film newFilm) {
        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            log.info("Фильм c id {} обновил название", newFilm.getId());
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()
                && !(newFilm.getDescription().length() > 200)) {
            log.info("Фильм с id {} обновил описание", newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null && !(newFilm.getReleaseDate().isBefore(CINEMA_BIRTH_DATE))) {
            log.info("Фильм с id {} обновил дату релиза", newFilm.getId());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
            log.info("Фильм с id {} обновил хронометраж", newFilm.getId());
            oldFilm.setDuration(newFilm.getDuration());
        }
    }
}

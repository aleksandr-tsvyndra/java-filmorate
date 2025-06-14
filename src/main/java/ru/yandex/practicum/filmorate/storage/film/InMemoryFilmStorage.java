package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate CINEMA_BIRTH_DATE;
    private static final int FILM_DESCRIPTION_LIMIT;

    static {
        CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);
        FILM_DESCRIPTION_LIMIT = 200;
    }

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        log.info("Получен http-запрос на получение списка всех фильмов");
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Получен http-запрос на добавление фильма");
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм с id {} был добавлен в базу данных", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Получен http-запрос на обновление фильма");
        if (films.containsKey(newFilm.getId())) {
            log.info("Фильм с id {} был найден в базе данных", newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            updateFields(oldFilm, newFilm);
            log.info("Успешно выполнен http-запрос на обновление фильма с id {}", newFilm.getId());
            return oldFilm;
        }
        var message = String.format("Фильма с id %d нет в базе данных", newFilm.getId());
        log.warn(message);
        throw new FilmNotFoundException(message);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public boolean isDateValid(LocalDate releaseDate) {
        return releaseDate.isAfter(CINEMA_BIRTH_DATE) || releaseDate.isEqual(CINEMA_BIRTH_DATE);
    }

    public boolean isDescriptionValid(String description) {
        return description.length() <= FILM_DESCRIPTION_LIMIT;
    }

    public boolean isDurationValid(Integer duration) {
        return duration > 0;
    }

    private void updateFields(Film oldFilm, Film newFilm) {
        if (Objects.nonNull(newFilm.getName()) && !newFilm.getName().isBlank()) {
            log.info("Фильм c id {} обновил название", newFilm.getId());
            oldFilm.setName(newFilm.getName());
        }
        if (Objects.nonNull(newFilm.getDescription()) && !newFilm.getDescription().isBlank()
                && isDescriptionValid(newFilm.getDescription())) {
            log.info("Фильм с id {} обновил описание", newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (Objects.nonNull(newFilm.getReleaseDate()) && isDateValid(newFilm.getReleaseDate())) {
            log.info("Фильм с id {} обновил дату релиза", newFilm.getId());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (Objects.nonNull(newFilm.getDuration()) && isDurationValid(newFilm.getDuration())) {
            log.info("Фильм с id {} обновил хронометраж", newFilm.getId());
            oldFilm.setDuration(newFilm.getDuration());
        }
    }
}

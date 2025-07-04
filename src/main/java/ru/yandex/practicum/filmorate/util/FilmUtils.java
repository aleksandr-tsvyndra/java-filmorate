package ru.yandex.practicum.filmorate.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmUtils {
    private static final LocalDate CINEMA_BIRTH_DATE;
    private static final int FILM_DESCRIPTION_LIMIT;

    static {
        CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);
        FILM_DESCRIPTION_LIMIT = 200;
    }

    public static void updateFields(Film oldFilm, UpdateFilmRequest newFilm) {
        if (newFilm.hasName()) {
            log.info("Фильм c id {} обновил название", newFilm.getId());
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.hasDescription() && isDescriptionValid(newFilm.getDescription())) {
            log.info("Фильм с id {} обновил описание", newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.hasReleaseDate() && isDateValid(newFilm.getReleaseDate())) {
            log.info("Фильм с id {} обновил дату релиза", newFilm.getId());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.hasDuration() && isDurationValid(newFilm.getDuration())) {
            log.info("Фильм с id {} обновил хронометраж", newFilm.getId());
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.hasMpa() && isMpaValid(newFilm.getMpa())) {
            log.info("Фильм с id {} обновил рейтинг", newFilm.getId());
            oldFilm.getMpa().setId(newFilm.getMpa().getId());
        }
        if (newFilm.hasGenres() && isGenresValid(newFilm.getGenres())) {
            log.info("Фильм с id {} обновил жанр", newFilm.getId());
            oldFilm.getGenres().addAll(newFilm.getGenres());
        }
    }

    public static boolean isDateValid(LocalDate releaseDate) {
        log.info("Проверяем, что дата выхода фильма не раньше {}", CINEMA_BIRTH_DATE);
        return releaseDate.isAfter(CINEMA_BIRTH_DATE) || releaseDate.isEqual(CINEMA_BIRTH_DATE);
    }

    public static boolean isDescriptionValid(String description) {
        log.info("Проверяем, что описание фильма не больше {} символов", FILM_DESCRIPTION_LIMIT);
        return description.length() <= FILM_DESCRIPTION_LIMIT;
    }

    public static boolean isDurationValid(Integer duration) {
        log.info("Проверяем, что хронометраж фильма больше 0");
        return duration > 0;
    }

    public static boolean isMpaValid(Rating mpa) {
        log.info("Проверяем, что id рейтинга в диапазоне от 1 до 5");
        return mpa.getId() > 0 && mpa.getId() < 6;
    }

    public static boolean isGenresValid(Set<Genre> filmGenres) {
        log.info("Проверяем, что id жанров в диапазоне от 1 до 6");
        for (Genre genre : filmGenres) {
            if (genre.getId() < 1 || genre.getId() > 6) {
                return false;
            }
        }
        return true;
    }
}
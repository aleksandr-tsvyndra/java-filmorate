package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Comparator<Film> FILM_LIKES_COMPARATOR;
    private static final LocalDate CINEMA_BIRTH_DATE;
    private static final int FILM_DESCRIPTION_LIMIT;

    static {
        FILM_LIKES_COMPARATOR = Comparator.comparing(film -> film.getLikes().size());
        CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);
        FILM_DESCRIPTION_LIMIT = 200;
    }

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film getById(long id) {
        Film filmById = filmStorage.findById(id);
        if (Objects.isNull(filmById)) {
            var message = String.format("Фильма с id %d нет в базе данных", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Фильм с id {} успешно найден", id);
        return filmById;
    }

    public Film update(Film newFilm) {
        Film oldFilm = getById(newFilm.getId());
        log.info("Фильм с id {} был найден в базе данных", newFilm.getId());
        updateFields(oldFilm, newFilm);
        log.info("Успешно выполнен http-запрос на обновление фильма с id {}", newFilm.getId());
        return oldFilm;
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Выводим юзеру список из {} самых популярных фильмов", count);
        return filmStorage.getAll()
                .stream()
                .sorted(FILM_LIKES_COMPARATOR.reversed())
                .limit(count)
                .toList();
    }

    public Film addLike(long id, long userId) {
        Film film = getById(id);
        User user = userStorage.findById(userId);
        if (Objects.isNull(user)) {
            var message = String.format("Юзера с id %d нет в базе данных", userId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        log.info("Юзер с id {} поставил лайк фильму с id {}", userId, id);
        return film;
    }

    public Film removeLike(long id, long userId) {
        Film film = getById(id);
        User user = userStorage.findById(userId);
        if (Objects.isNull(user)) {
            var message = String.format("Юзера с id %d нет в базе данных", userId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        log.info("Юзер с id {} удалил лайк фильму с id {}", userId, id);
        return film;
    }

    private boolean isDateValid(LocalDate releaseDate) {
        log.info("Проверяем, что дата выхода фильма не раньше {}", CINEMA_BIRTH_DATE);
        return releaseDate.isAfter(CINEMA_BIRTH_DATE) || releaseDate.isEqual(CINEMA_BIRTH_DATE);
    }

    private boolean isDescriptionValid(String description) {
        log.info("Проверяем, что описание фильма не больше {} символов", FILM_DESCRIPTION_LIMIT);
        return description.length() <= FILM_DESCRIPTION_LIMIT;
    }

    private boolean isDurationValid(Integer duration) {
        log.info("Проверяем, что хронометраж фильма больше 0");
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

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Comparator<Film> FILM_LIKES_COMPARATOR;

    static {
        FILM_LIKES_COMPARATOR = Comparator.comparing(film -> film.getLikes().size());
    }

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
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

    public Film update(UpdateFilmRequest request) {
        Film oldFilm = getById(request.getId());
        log.info("Фильм с id {} был найден в базе данных", oldFilm.getId());
        filmStorage.update(oldFilm, request);
        log.info("Успешно выполнен http-запрос на обновление фильма с id {}", oldFilm.getId());
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
        filmStorage.addLike(film, userId);
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
        filmStorage.removeLike(film, userId);
        log.info("Юзер с id {} удалил лайк фильму с id {}", userId, id);
        return film;
    }
}

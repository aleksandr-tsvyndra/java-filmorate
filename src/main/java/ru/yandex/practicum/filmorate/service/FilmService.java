package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeStorage;
import ru.yandex.practicum.filmorate.dal.rating.RatingStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final LikeStorage likeStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll()
                .stream()
                .peek(this::setFilmGenres)
                .peek(this::setFilmLikes)
                .collect(Collectors.toList());
    }

    public Film create(NewFilmRequest request) {
        Film film = filmStorage.create(FilmMapper.mapToFilm(request));
        if (Objects.nonNull(film.getMpa())) {
            checkMpa(film);
            ratingStorage.updateFilmRating(film.getMpa().getId(), film.getId());
        }
        if (Objects.nonNull(film.getGenres())) {
            checkGenres(film);
            genreStorage.addToFilmGenres(film);
        }
        return film;
    }

    public Film getById(long id) {
        Film filmById = filmStorage.findById(id);
        if (Objects.isNull(filmById)) {
            var message = String.format("Фильма с id %d нет в базе данных", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        setFilmGenres(filmById);
        setFilmLikes(filmById);
        log.info("Фильм с id {} успешно найден", id);
        return filmById;
    }

    public Film update(UpdateFilmRequest request) {
        Film film = getById(request.getId());
        log.info("Фильм с id {} был найден в базе данных", film.getId());
        updateFields(film, request);
        filmStorage.update(film);
        updateFilmGenres(film);
        log.info("Успешно выполнен http-запрос на обновление фильма с id {}", film.getId());
        return film;
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Выводим юзеру список из {} самых популярных фильмов", count);
        return getAll()
                .stream()
                .sorted(FILM_LIKES_COMPARATOR.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addLike(long id, long userId) {
        Film film = getById(id);
        User user = userStorage.findById(userId);
        if (Objects.isNull(user)) {
            var message = String.format("Юзера с id %d нет в базе данных", userId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        film.getLikes().add(userId);
        likeStorage.add(film.getId(), userId);
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
        film.getLikes().remove(userId);
        likeStorage.remove(film.getId(), userId);
        log.info("Юзер с id {} удалил лайк фильму с id {}", userId, id);
        return film;
    }

    private void checkMpa(Film film) {
        Integer mpaId = film.getMpa().getId();
        Rating mpa = ratingStorage.findById(mpaId);
        if (Objects.isNull(mpa)) {
            throw new NotFoundException(String.format("MPA с id %d в базе данных нет", mpaId));
        }
    }

    private void checkGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            if (Objects.isNull(genreStorage.findById(genre.getId()))) {
                throw new NotFoundException(String.format("Жанра с id %d в базе данных нет", genre.getId()));
            }
        }
    }

    private void updateFilmGenres(Film film) {
        genreStorage.removeFilmGenres(film);
        genreStorage.addToFilmGenres(film);
    }

    private void setFilmGenres(Film film) {
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
    }

    private void setFilmLikes(Film film) {
        Set<Long> filmLikes = likeStorage.getFilmLikes(film.getId())
                .stream()
                .map(FilmLike::getUserId)
                .collect(Collectors.toSet());
        for (Long like : filmLikes) {
            film.getLikes().add(like);
        }
    }

    private static void updateFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            log.info("Фильм c id {} обновил название", request.getId());
            film.setName(request.getName());
        }
        if (request.hasDescription() && isDescriptionValid(request.getDescription())) {
            log.info("Фильм с id {} обновил описание", request.getId());
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate() && isDateValid(request.getReleaseDate())) {
            log.info("Фильм с id {} обновил дату релиза", request.getId());
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration() && isDurationValid(request.getDuration())) {
            log.info("Фильм с id {} обновил хронометраж", request.getId());
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa() && isMpaValid(request.getMpa())) {
            log.info("Фильм с id {} обновил рейтинг", request.getId());
            film.getMpa().setId(request.getMpa().getId());
        }
        if (request.hasGenres() && isGenresValid(request.getGenres())) {
            log.info("Фильм с id {} обновил жанр", request.getId());
            film.getGenres().addAll(request.getGenres());
        }
    }

    private static boolean isDateValid(LocalDate releaseDate) {
        log.info("Проверяем, что дата выхода фильма не раньше {}", CINEMA_BIRTH_DATE);
        return releaseDate.isAfter(CINEMA_BIRTH_DATE) || releaseDate.isEqual(CINEMA_BIRTH_DATE);
    }

    private static boolean isDescriptionValid(String description) {
        log.info("Проверяем, что описание фильма не больше {} символов", FILM_DESCRIPTION_LIMIT);
        return description.length() <= FILM_DESCRIPTION_LIMIT;
    }

    private static boolean isDurationValid(Integer duration) {
        log.info("Проверяем, что хронометраж фильма больше 0");
        return duration > 0;
    }

    private static boolean isMpaValid(Rating mpa) {
        log.info("Проверяем, что id рейтинга в диапазоне от 1 до 5");
        return mpa.getId() > 0 && mpa.getId() < 6;
    }

    private static boolean isGenresValid(Set<Genre> filmGenres) {
        log.info("Проверяем, что id жанров в диапазоне от 1 до 6");
        for (Genre genre : filmGenres) {
            if (genre.getId() < 1 || genre.getId() > 6) {
                return false;
            }
        }
        return true;
    }
}

package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.dal.BaseStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeStorage;
import ru.yandex.practicum.filmorate.dal.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.dal.rating.RatingStorage;
import ru.yandex.practicum.filmorate.util.FilmUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Primary
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name AS rating_name " +
            "FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.name AS rating_name " +
            "FROM films AS f LEFT JOIN ratings AS r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films " +
            "(name, description, release_date, duration) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String UPDATE_FILM_RATING_QUERY = "UPDATE films SET rating_id = ? WHERE film_id = ?";

    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Film> filmMapper,
            RowMapper<Genre> genreMapper,
            RowMapper<FilmLike> likeMapper,
            RowMapper<Rating> ratingMapper
    ) {
        super(jdbc, filmMapper);
        genreStorage = new GenreDbStorage(jdbc, genreMapper);
        likeStorage = new LikeDbStorage(jdbc, likeMapper);
        ratingStorage = new RatingDbStorage(jdbc, ratingMapper);
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film findById(long id) {
        Optional<Film> result = findOne(FIND_BY_ID_QUERY, id);
        return result.orElse(null);
    }

    @Override
    public Film create(Film film) {
        Long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        film.setId(id);
        if (Objects.nonNull(film.getMpa())) {
            checkMpa(film);
            update(UPDATE_FILM_RATING_QUERY, film.getMpa().getId(), film.getId());
        }
        if (Objects.nonNull(film.getGenres())) {
            checkGenres(film);
            genreStorage.addToFilmGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film oldFilm, UpdateFilmRequest newFilm) {
        FilmUtils.updateFields(oldFilm, newFilm);
        update(UPDATE_QUERY,
                oldFilm.getName(),
                oldFilm.getDescription(),
                oldFilm.getReleaseDate(),
                oldFilm.getDuration(),
                oldFilm.getMpa().getId(),
                oldFilm.getId());
        updateFilmGenres(oldFilm);
        return oldFilm;
    }

    @Override
    public void addLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        likeStorage.add(film.getId(), userId);
    }

    @Override
    public void removeLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        likeStorage.remove(film.getId(), userId);
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
}

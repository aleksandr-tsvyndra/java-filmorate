package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dal.BaseStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = "SELECT * FROM genres WHERE genre_id IN " +
            "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAll() {
        return findMany(FIND_ALL_QUERY)
                .stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    @Override
    public Genre findById(Integer genreId) {
        Optional<Genre> result = findOne(FIND_BY_ID_QUERY, genreId);
        return result.orElse(null);
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        return new HashSet<>(findMany(FIND_GENRES_BY_FILM_ID_QUERY, filmId));
    }

    @Override
    public void addToFilmGenres(Film film) {
        film.getGenres().forEach(genre -> insert(INSERT_QUERY, film.getId(), genre.getId()));
    }

    @Override
    public void removeFilmGenres(Film film) {
        film.getGenres().forEach(genre -> delete(DELETE_FILM_GENRE_QUERY, film.getId()));
    }
}

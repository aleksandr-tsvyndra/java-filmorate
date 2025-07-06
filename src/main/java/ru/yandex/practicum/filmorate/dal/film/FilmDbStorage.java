package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

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

    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Film> filmMapper
    ) {
        super(jdbc, filmMapper);
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
        return film;
    }

    @Override
    public Film update(Film oldFilm) {
        update(UPDATE_QUERY,
                oldFilm.getName(),
                oldFilm.getDescription(),
                oldFilm.getReleaseDate(),
                oldFilm.getDuration(),
                oldFilm.getMpa().getId(),
                oldFilm.getId());
        return oldFilm;
    }
}
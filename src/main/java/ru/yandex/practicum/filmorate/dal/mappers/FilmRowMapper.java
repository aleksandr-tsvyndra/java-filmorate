package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeStorage;
import ru.yandex.practicum.filmorate.dal.rating.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final RatingStorage ratingStorage;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(LocalDate.parse(resultSet.getString("release_date")));
        film.setDuration(resultSet.getInt("duration"));

        film.setMpa(ratingStorage.findById(resultSet.getInt("rating_id")));

        film.setGenres(genreStorage.getFilmGenres(film.getId()));

        film.getLikes().addAll(likeStorage.getFilmLikes(film.getId())
                .stream()
                .map(FilmLike::getUserId)
                .collect(Collectors.toSet()));

        return film;
    }
}

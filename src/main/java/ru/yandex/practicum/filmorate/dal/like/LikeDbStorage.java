package ru.yandex.practicum.filmorate.dal.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.dal.BaseStorage;

import java.util.HashSet;
import java.util.Set;

@Repository
public class LikeDbStorage extends BaseStorage<FilmLike> implements LikeStorage {
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT * FROM film_likes WHERE film_id = ?";

    @Autowired
    public LikeDbStorage (JdbcTemplate jdbc, RowMapper<FilmLike> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Set<FilmLike> getFilmLikes(Long filmId) {
        return new HashSet<>(findMany(FIND_FILM_LIKES_QUERY, filmId));
    }

    @Override
    public void add(Long filmId, Long userId) {
        insert(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void remove(Long filmId, Long userId) {
        delete(DELETE_LIKE_QUERY, filmId, userId);
    }
}

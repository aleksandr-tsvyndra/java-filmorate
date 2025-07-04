package ru.yandex.practicum.filmorate.dal.like;

import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.Set;

public interface LikeStorage {
    Set<FilmLike> getFilmLikes(Long filmId);

    void add(Long filmId, Long userId);

    void remove(Long filmId, Long userId);
}

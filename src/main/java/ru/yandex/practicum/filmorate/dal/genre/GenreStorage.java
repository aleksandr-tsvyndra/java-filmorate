package ru.yandex.practicum.filmorate.dal.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAll();

    Genre findById(Integer genreId);

    Set<Genre> getFilmGenres(Long filmId);

    void addToFilmGenres(Film film);

    void removeFilmGenres(Film film);
}

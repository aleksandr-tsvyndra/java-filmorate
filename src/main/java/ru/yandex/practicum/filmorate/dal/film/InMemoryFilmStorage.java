package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film findById(long id) {
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film oldFilm, UpdateFilmRequest newFilm) {
        FilmUtils.updateFields(oldFilm, newFilm);
        return oldFilm;
    }

    @Override
    public void addLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        likes.add(userId);
    }

    @Override
    public void removeLike(Film film, long userId) {
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

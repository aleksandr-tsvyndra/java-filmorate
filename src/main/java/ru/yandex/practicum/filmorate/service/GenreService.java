package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(Integer genreId) {
        Genre genre = genreStorage.findById(genreId);
        if (Objects.isNull(genre)) {
            var message = String.format("Жанра с id %d нет в базе данных", genreId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Жанр с id {} успешно найден", genreId);
        return genre;
    }
}

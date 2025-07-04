package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Получен http-запрос на получение списка всех жанров");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable("id") Integer genreId) {
        log.info("Получен http-запрос на получение жанра с id {}", genreId);
        return genreService.getById(genreId);
    }
}

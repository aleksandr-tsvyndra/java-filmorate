package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> getAll() {
        log.info("Получен http-запрос на получение списка всех рейтингов");
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public Rating getById(@PathVariable("id") Integer ratingId) {
        log.info("Получен http-запрос на получение рейтинга с id {}", ratingId);
        return ratingService.getById(ratingId);
    }
}

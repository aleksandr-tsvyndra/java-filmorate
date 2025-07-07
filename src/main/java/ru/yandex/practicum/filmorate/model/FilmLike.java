package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmLike {
    private Long id;
    private Long filmId;
    private Long userId;
}

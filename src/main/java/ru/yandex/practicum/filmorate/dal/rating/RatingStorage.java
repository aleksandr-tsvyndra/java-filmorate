package ru.yandex.practicum.filmorate.dal.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

public interface RatingStorage {
    Collection<Rating> getAll();

    Rating findById(Integer ratingId);
}

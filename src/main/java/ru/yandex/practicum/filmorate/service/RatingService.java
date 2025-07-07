package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.dal.rating.RatingStorage;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<Rating> getAll() {
        return ratingStorage.getAll();
    }

    public Rating getById(Integer ratingId) {
        Rating rating = ratingStorage.findById(ratingId);
        if (Objects.isNull(rating)) {
            var message = String.format("Рейтинга с id %d нет в базе данных", ratingId);
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Рейтинг с id {} успешно найден", ratingId);
        return rating;
    }
}

package ru.yandex.practicum.filmorate.dal.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.dal.BaseStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Repository
public class RatingDbStorage extends BaseStorage<Rating> implements RatingStorage {
    private static final String FIND_RATING_QUERY = "SELECT * FROM ratings WHERE rating_id = ?";
    private static final String FIND_ALL_RATING_QUERY = "SELECT * FROM ratings";

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Rating> getAll() {
        return findMany(FIND_ALL_RATING_QUERY)
                .stream()
                .sorted(Comparator.comparing(Rating::getId))
                .toList();
    }

    @Override
    public Rating findById(Integer ratingId) {
        Optional<Rating> result = findOne(FIND_RATING_QUERY, ratingId);
        return result.orElse(null);
    }
}

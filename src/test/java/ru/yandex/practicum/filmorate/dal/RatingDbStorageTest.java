package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dal.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingDbStorage.class, RatingRowMapper.class})
public class RatingDbStorageTest {
    private final RatingRowMapper mapper;
    private final RatingDbStorage ratingStorage;

    @Test
    @DirtiesContext
    void testGetAllRatings() {
        List<Rating> ratings = ratingStorage.getAll().stream().toList();

        assertThat(ratings).isNotNull();
        assertThat(ratings.size()).isEqualTo(5);

        assertThat(ratings.get(0).getName()).isEqualTo("G");
        assertThat(ratings.get(1).getName()).isEqualTo("PG");
        assertThat(ratings.get(2).getName()).isEqualTo("PG-13");
        assertThat(ratings.get(3).getName()).isEqualTo("R");
        assertThat(ratings.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    @DirtiesContext
    void testFindById() {
        Rating mpaG = new Rating();
        mpaG.setId(1);
        mpaG.setName("G");

        Rating foundMpa = ratingStorage.findById(mpaG.getId());

        assertThat(foundMpa).isNotNull();
        assertThat(foundMpa.getName()).isEqualTo(mpaG.getName());

        Rating mpaPG13 = new Rating();
        mpaPG13.setId(3);
        mpaPG13.setName("PG-13");

        foundMpa = ratingStorage.findById(mpaPG13.getId());

        assertThat(foundMpa).isNotNull();
        assertThat(foundMpa.getName()).isEqualTo(mpaPG13.getName());

        Rating mpaPG17 = new Rating();
        mpaPG17.setId(5);
        mpaPG17.setName("NC-17");

        foundMpa = ratingStorage.findById(mpaPG17.getId());

        assertThat(foundMpa).isNotNull();
        assertThat(foundMpa.getName()).isEqualTo(mpaPG17.getName());
    }
}

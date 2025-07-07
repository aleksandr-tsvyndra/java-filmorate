package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.dal.like.LikeStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.dal.rating.RatingStorage;
import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmDbStorage.class,
        GenreDbStorage.class,
        LikeDbStorage.class,
        RatingDbStorage.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        LikeRowMapper.class,
        RatingRowMapper.class,
        UserDbStorage.class,
        UserRowMapper.class,
        FriendshipRowMapper.class
})
public class FilmDbStorageTest {
    private final UserRowMapper userMapper;
    private final FriendshipRowMapper friendshipMapper;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final RatingStorage ratingStorage;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final LikeRowMapper likeRowMapper;
    private final RatingRowMapper ratingRowMapper;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    @DirtiesContext
    void testCreateFilm() {
        var film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Film savedFilm = filmStorage.create(film);

        assertThat(savedFilm).isNotNull();
        assertEquals(1L, savedFilm.getId());
        assertThat(savedFilm).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(savedFilm).hasFieldOrPropertyWithValue("description", "Test Description");
        assertThat(savedFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 1));
        assertThat(savedFilm).hasFieldOrPropertyWithValue("duration", 120);
    }

    @Test
    @DirtiesContext
    void testFindFilmById() {
        var film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        filmStorage.create(film);

        Film foundFilm = filmStorage.findById(1L);

        assertThat(foundFilm).isNotNull();
        assertEquals(1L, foundFilm.getId());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(foundFilm).hasFieldOrPropertyWithValue("description", "Test Description");
        assertThat(foundFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 1));
        assertThat(foundFilm).hasFieldOrPropertyWithValue("duration", 120);
    }

    @Test
    @DirtiesContext
    void testGetAllFilms() {
        filmStorage.create(new Film(0L, "Pulp Fiction", "asdsdfsf",
                LocalDate.parse("1994-07-23"), 126, null, null));
        filmStorage.create(new Film(0L, "Inception", "asdsfsdff",
                LocalDate.parse("2011-07-23"), 126, null, null));
        filmStorage.create(new Film(0L, "Django Unchained", "ashklhkfsf",
                LocalDate.parse("2012-12-20"), 126, null, null));

        Collection<Film> films = filmStorage.getAll();

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(3);
    }

    @Test
    @DirtiesContext
    void testUpdateFilm() {
        var film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        var mpa = new Rating();
        mpa.setId(1);
        film.setMpa(mpa);
        film.setGenres(new HashSet<>());
        var savedFilm = filmStorage.create(film);

        savedFilm.setName("Updated Film");
        savedFilm.setDuration(260);
        filmStorage.update(savedFilm);

        Film updatedFilm = filmStorage.findById(savedFilm.getId());

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Updated Film");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "Test Description");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 1));
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", 260);
    }

    @Test
    @DirtiesContext
    void testAddAndRemoveFilmLike() {
        var user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.create(user);

        var film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        Film savedFilm = filmStorage.create(film);

        likeStorage.add(savedFilm.getId(), savedUser.getId());

        Set<FilmLike> likesWithOneLike = likeStorage.getFilmLikes(savedFilm.getId());
        assertThat(likesWithOneLike).isNotNull();
        assertThat(likesWithOneLike.size()).isEqualTo(1);

        likeStorage.remove(savedFilm.getId(), savedUser.getId());

        Set<FilmLike> likesWithNoLike = likeStorage.getFilmLikes(savedFilm.getId());
        assertThat(likesWithNoLike).isNotNull();
        assertThat(likesWithNoLike.size()).isEqualTo(0);
    }
}

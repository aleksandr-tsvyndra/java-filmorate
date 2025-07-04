//package ru.yandex.practicum.filmorate.dal;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
//import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
//import ru.yandex.practicum.filmorate.model.Genre;
//
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@JdbcTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@Import({GenreDbStorage.class, GenreRowMapper.class})
//public class GenreDbStorageTest {
//    private final GenreRowMapper mapper;
//    private final GenreDbStorage genreStorage;
//
//    @Test
//    @DirtiesContext
//    void testGetAllRatings() {
//        List<Genre> genres = genreStorage.getAll().stream().toList();
//
//        assertThat(genres).isNotNull();
//        assertThat(genres.size()).isEqualTo(6);
//
//        assertThat(genres.get(0).getName()).isEqualTo("Комедия");
//        assertThat(genres.get(1).getName()).isEqualTo("Драма");
//        assertThat(genres.get(2).getName()).isEqualTo("Мультфильм");
//        assertThat(genres.get(3).getName()).isEqualTo("Триллер");
//        assertThat(genres.get(4).getName()).isEqualTo("Документальный");
//        assertThat(genres.get(5).getName()).isEqualTo("Боевик");
//    }
//
//    @Test
//    @DirtiesContext
//    void testFindById() {
//        Genre comedy = new Genre();
//        comedy.setId(1);
//        comedy.setName("Комедия");
//
//        Genre foundGenre = genreStorage.findById(comedy.getId());
//
//        assertThat(foundGenre).isNotNull();
//        assertThat(foundGenre.getName()).isEqualTo(comedy.getName());
//
//        Genre animation = new Genre();
//        animation.setId(3);
//        animation.setName("Мультфильм");
//
//        foundGenre = genreStorage.findById(animation.getId());
//
//        assertThat(foundGenre).isNotNull();
//        assertThat(foundGenre.getName()).isEqualTo(animation.getName());
//
//        Genre action = new Genre();
//        action.setId(6);
//        action.setName("Боевик");
//
//        foundGenre = genreStorage.findById(action.getId());
//
//        assertThat(foundGenre).isNotNull();
//        assertThat(foundGenre.getName()).isEqualTo(action.getName());
//    }
//}

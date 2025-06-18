package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {

    private FilmController filmController;
    private FilmService filmService;
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void create_whenFilmIsValid_returnsFilmWithId() {
        int expectedId = 1;

        var actual = filmController.create(new Film());

        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
    }

    @Test
    public void getById_whenFilmPresent_returnsFilm() {
        var createdFilm = filmController.create(new Film(0L, "nisi eiusmod", "adipisicing",
                LocalDate.parse("1967-03-25"), 100));
        long presentId = createdFilm.getId();

        var actual = filmController.getById(presentId);

        assertNotNull(actual);
        assertEquals(createdFilm.getName(), actual.getName());
        assertEquals(createdFilm.getDescription(), actual.getDescription());
        assertEquals(createdFilm.getReleaseDate(), actual.getReleaseDate());
        assertEquals(createdFilm.getDuration(), actual.getDuration());
    }

    @Test
    public void getById_whenFilmNotPresent_throwsException() {
        var createdFilm = filmController.create(new Film());
        long notPresentId = createdFilm.getId() + 1;

        assertThrows(FilmNotFoundException.class, () -> {
            filmController.getById(notPresentId);
        });
    }

    @Test
    public void getAll_whenNoFilms_returnsEmptyList() {
        int expectedSize = 0;

        var actual = filmController.getAll();

        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void getAll_whenThereIsFilm_returnsListWithFilm() {
        int expectedSize = 1;
        filmController.create(new Film());

        var actual = filmController.getAll();

        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    public void update_whenIdNotValid_throwsException() {
        var createdFilm = filmController.create(new Film());
        // создаем Film с id, которого нет в мапе контроллера
        long notValidId = createdFilm.getId() + 1;
        var updateFilm = new Film();
        updateFilm.setId(notValidId);

        assertThrows(FilmNotFoundException.class, () -> {
            filmController.update(updateFilm);
        });
    }

    @Test
    public void update_whenIdValid_returnsUpdatedFilm() {
        var oldFilm = filmController.create(new Film(1L, "Форрест Гамп", "adipisicing",
                LocalDate.parse("1967-03-25"), 100));
        // создаем Film с id, который есть в мапе контроллера
        // и задаем ему другие поля
        long validId = oldFilm.getId();
        var newFilm = new Film(validId, "Криминальное чтиво", "qwertyuiop",
                LocalDate.parse("2011-09-15"), 220);

        filmController.update(newFilm);

        assertEquals(newFilm.getName(), oldFilm.getName());
        assertEquals(newFilm.getDescription(), oldFilm.getDescription());
        assertEquals(newFilm.getReleaseDate(), oldFilm.getReleaseDate());
        assertEquals(newFilm.getDuration(), oldFilm.getDuration());
    }

    @Test
    public void update_whenFieldNullOrEmpty_shouldNotUpdateField() {
        var oldFilm = filmController.create(new Film(1L, "Форрест Гамп", "adipisicing",
                LocalDate.parse("1967-03-25"), 100));
        // создаем Film с id, который есть в хранилище
        // значение его полей name и releasedDate будут null
        long validId = oldFilm.getId();
        var newFilm = new Film(validId, null, "qwertyuiop", null, 220);

        filmController.update(newFilm);

        assertNotEquals(newFilm.getName(), oldFilm.getName());
        assertEquals(newFilm.getDescription(), oldFilm.getDescription());
        assertNotEquals(newFilm.getReleaseDate(), oldFilm.getReleaseDate());
        assertEquals(newFilm.getDuration(), oldFilm.getDuration());
    }

    @Test
    public void getPopularFilms_whenFilmsPresent_returnsDescSortedList() {
        // добавляем в хранилище 4 фильма
        int filmsCount = 4;
        for (int i = 0; i < filmsCount; i++) {
            filmController.create(new Film());
        }

        // устанавливаем "вручную" каждому фильму разное количество лайков
        // у фильма с id 1 будет 1 лайк
        var oneLike = filmController.getById(1);
        oneLike.getLikes().add(22L);
        // у фильма с id 2 будет 2 лайка
        var twoLikes = filmController.getById(2);
        twoLikes.getLikes().add(22L);
        twoLikes.getLikes().add(23L);
        // у фильма с id 3 будет 3 лайка
        var threeLikes = filmController.getById(3);
        threeLikes.getLikes().add(22L);
        threeLikes.getLikes().add(23L);
        threeLikes.getLikes().add(24L);
        // у фильма с id 4 не будет ни одного лайка

        var popularFilms = filmController.getPopularFilms(filmsCount).toArray(new Film[filmsCount]);

        assertNotNull(popularFilms);
        assertEquals(3, popularFilms[0].getLikes().size()); // первым идет фильм с 3 лайками
        assertEquals(2, popularFilms[1].getLikes().size()); // вторым идет фильм с 2 лайками
        assertEquals(1, popularFilms[2].getLikes().size()); // третьим идет фильм с 1 лайком
        assertEquals(0, popularFilms[3].getLikes().size()); // последним идет фильм без лайков
    }

    @Test
    public void addLike_returnsFilmWithLike() {
        var user = userStorage.create(new User(0L, "a@mail.com", "A1Ar", "Audrey",
                LocalDate.parse("1967-03-25")));
        var createdFilm = filmController.create(new Film());

        assertEquals(0, createdFilm.getLikes().size());

        filmController.addLike(createdFilm.getId(), user.getId());

        assertEquals(1, createdFilm.getLikes().size());
        assertTrue(createdFilm.getLikes().contains(user.getId()));
    }

    @Test
    public void addLike_whenFilmNotPresent_throwsException() {
        var user = userStorage.create(new User(0L, "a@mail.com", "A1Ar", "Audrey",
                LocalDate.parse("1967-03-25")));
        var notPresentId = 9L;

        assertThrows(FilmNotFoundException.class, () -> {
            filmController.addLike(notPresentId, user.getId());
        });
    }

    @Test
    public void addLike_whenUserNotPresent_throwsException() {
        var createdFilm = filmController.create(new Film());
        var notPresentId = 29L;

        assertThrows(UserNotFoundException.class, () -> {
            filmController.addLike(createdFilm.getId(), notPresentId);
        });
    }

    @Test
    public void removeLike_returnsFilmWithNoLike() {
        var user = userStorage.create(new User(0L, "a@mail.com", "A1Ar", "Audrey",
                LocalDate.parse("1967-03-25")));
        var createdFilm = filmController.create(new Film());
        createdFilm.getLikes().add(user.getId());

        assertEquals(1, createdFilm.getLikes().size());

        filmController.removeLike(createdFilm.getId(), user.getId());

        assertEquals(0, createdFilm.getLikes().size());
    }

    @Test
    public void removeLike_whenFilmNotPresent_throwsException() {
        var user = userStorage.create(new User(0L, "a@mail.com", "A1Ar", "Audrey",
                LocalDate.parse("1967-03-25")));
        var notPresentId = 9L;

        assertThrows(FilmNotFoundException.class, () -> {
            filmController.removeLike(notPresentId, user.getId());
        });
    }

    @Test
    public void removeLike_whenUserNotPresent_throwsException() {
        var createdFilm = filmController.create(new Film());
        var notPresentId = 29L;

        assertThrows(UserNotFoundException.class, () -> {
            filmController.removeLike(createdFilm.getId(), notPresentId);
        });
    }
}
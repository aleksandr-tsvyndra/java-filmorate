package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void create_whenFilmIsValid_returnsFilmWithId() {
        int expectedId = 1;

        var actual = filmController.create(new Film());

        assertNotNull(actual);
        assertEquals(expectedId, actual.getId());
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
        // создаем Film с id, который есть в мапе контроллера
        // значение его полей name и releasedDate будут null
        long validId = oldFilm.getId();
        var newFilm = new Film(validId, null, "qwertyuiop", null, 220);

        filmController.update(newFilm);

        assertNotEquals(newFilm.getName(), oldFilm.getName());
        assertEquals(newFilm.getDescription(), oldFilm.getDescription());
        assertNotEquals(newFilm.getReleaseDate(), oldFilm.getReleaseDate());
        assertEquals(newFilm.getDuration(), oldFilm.getDuration());
    }
}
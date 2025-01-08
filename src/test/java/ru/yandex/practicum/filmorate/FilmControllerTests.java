package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.User.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {
    FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController(new InMemoryFilmStorage(), new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    public void checkGetFilms() {
        assertEquals(0, filmController.findAll().size());
        Film film = new Film(LocalDate.of(2014, 11, 7), Duration.ofMinutes(169));
        film.setName("Интерстеллар");
        Film createdFilm = filmController.create(film);
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void checkFilmValidation() {
        Film film = new Film(LocalDate.of(2014, 11, 7), Duration.ofMinutes(169));
        film.setName("Интерстеллар");
        film.setDescription("ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа");//здесь 201 символ в описании
        assertThrows(ValidationException.class, () -> filmController.create(film));
        film.setDescription("1");
        assertDoesNotThrow(() -> filmController.create(film));
    }

}

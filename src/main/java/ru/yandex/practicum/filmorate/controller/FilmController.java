package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Film.InMemoryFilmStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел Get запрос /films");
        Collection<Film> films = filmStorage.getFilms();
        log.info("Отправлен ответ GET /films с телом: {}", films);
        return films;
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        log.info("Пришел GET запрос /films/{}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        log.info("Отправлен ответ GET /films/{} с телом: {} ", filmId, film);
        return film;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Пришел GET запрос /films/popular?count={}", count);
        List<Film> films = filmService.getMostLikedFilms(count);
        log.info("Отправлен ответ GET /films/popular?count={} с телом: {}", count, films);
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("пришел Post запрос /films с телом: {}", film);
        Film newFilm = filmStorage.addFilm(film);
        log.info("Отправлен ответ Post /films с телом: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("пришел Put запрос /films с телом: {}", film);
        Film updatedFilm = filmStorage.updateFilm(film.getId());
        log.info("Отправлен Put запрос /films с телом: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film like(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Пришел PUT запрос /films/{}/like/{}", filmId, userId);
        Film film = filmService.userLikedFilm(filmId, userId);
        log.info("Отправлен ответ PUT /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film unlike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Пришел DELETE запрос /films/{}/like/{}", filmId, userId);
        Film film = filmService.userUnlikeFilm(filmId, userId);
        log.info("Отправлен ответ DELETE /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }
}

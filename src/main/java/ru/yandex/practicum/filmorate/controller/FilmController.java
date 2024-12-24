package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmCounter = 0;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел Get запрос /films");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("пришел Post запрос /films с телом: {}", film);
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        log.debug("Длительность фильма изменена на минуты");
        film.setId(getNextId());
        log.debug("Получен новый id фильма: {}", film.getId());
        films.put(film.getId(), film);
        log.info("Отправлен ответ Post /films с телом: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("пришел Put запрос /films с телом: {}", film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильма с id = {} не существует", film.getId());
            throw new NotFoundException("Фильма с данным id не существует");
        }
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        log.debug("Длительность фильма изменена на минуты");
        films.put(film.getId(), film);
        log.info("Отправлен Put запрос /films с телом: {}", film);
        return film;
    }

    private boolean isFilmValid(Film filmToCheck) {
        return ((filmToCheck.getDescription() == null || filmToCheck.getDescription().length() < 200) &&
                filmToCheck.getReleaseDate().isAfter(MIN_FILM_DATE) &&
                filmToCheck.getDuration().isPositive());
    }

    private int getNextId() {
        return ++filmCounter;
    }
}

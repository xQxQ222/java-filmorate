package ru.yandex.practicum.filmorate.storage.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films;
    private int filmCounter = 0;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        film.setId(getNextId());
        log.debug("Получен новый id фильма: {}", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    @Override
    public Film updateFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильма с id = {} не существует", filmId);
            throw new NotFoundException("Фильма с данным id не существует");
        }
        Film film = films.get(filmId);
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        films.put(film.getId(), film);
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

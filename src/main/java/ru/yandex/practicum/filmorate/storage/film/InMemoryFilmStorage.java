package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@Qualifier("filmInMemory")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int filmCounter = 0;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    public List<Film> getFilms() {
        return films.values().stream().toList();
    }

    @Override
    public Film addFilm(Film film) {
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        film.setId(getNextId());
        log.debug("Получен новый id фильма: {}", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (!films.containsKey(filmId)) {
            log.error("Фильма с id = {} не существует", filmId);
            throw new NotFoundException("Фильма с данным id не существует");
        }
        film.setDuration(Duration.ofMinutes(film.getDuration().getSeconds()));
        films.put(film.getId(), film);
        return film;
    }


    private int getNextId() {
        return ++filmCounter;
    }
}

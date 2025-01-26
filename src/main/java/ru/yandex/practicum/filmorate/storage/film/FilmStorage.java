package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getFilms();

    Film updateFilm(Film film);

    Film addFilm(Film newFilm);

    Optional<Film> getFilmById(int filmId);
}

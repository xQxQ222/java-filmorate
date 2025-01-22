package ru.yandex.practicum.filmorate.storage.Film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film updateFilm(Film film);

    Film addFilm(Film newFilm);

    Optional<Film> getFilmById(int filmId);
}

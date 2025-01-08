package ru.yandex.practicum.filmorate.storage.Film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film updateFilm(int filmId);

    Film addFilm(Film newFilm);

    Film getFilmById(int filmId);
}
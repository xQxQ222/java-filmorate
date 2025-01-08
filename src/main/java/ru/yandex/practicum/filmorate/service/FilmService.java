package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.User.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film userLikedFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId);//исключаем ошибку некорректного пользователя
        Film film = filmStorage.getFilmById(filmId);
        if (film.getUserLiked().contains(userId)) {
            throw new LikeFilmException("Фильм уже был лайкнут этим пользователем", userId, filmId);
        }
        film.getUserLiked().add(userId);
        return film;
    }

    public Film userUnlikeFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId);//исключаем ошибку некорректного пользователя
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getUserLiked().contains(userId)) {
            throw new LikeFilmException("Данный фильм не был до этого лайкнут пользователем", userId, filmId);
        }
        film.getUserLiked().remove(userId);
        return film;
    }

    public List<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество выбранных фильмов должно быть больше 0");
        }
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUserLiked().size()).reversed())
                .limit(count)
                .toList();
    }
}

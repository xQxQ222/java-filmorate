package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film userLikedFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId);//исключаем ошибку некорректного пользователя
        Film film = filmStorage.getFilmById(filmId);
        if (film.getUserLiked().contains(userId)) {
            throw new LikeFilmException("Фильм уже был лайкнут этим пользователем", userId, filmId);
        }
        film.addLike(userId);
        return film;
    }

    public Film userUnlikeFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId);//исключаем ошибку некорректного пользователя
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getUserLiked().contains(userId)) {
            throw new LikeFilmException("Данный фильм не был до этого лайкнут пользователем", userId, filmId);
        }
        film.removeLike(userId);
        return film;
    }

    public List<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество выбранных фильмов должно быть больше 0");
        }
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingLong(Film::getRate).reversed())
                .limit(count)
                .toList();
    }

    private boolean isFilmValid(Film filmToCheck) {
        return ((filmToCheck.getDescription() == null || filmToCheck.getDescription().length() < 200) &&
                filmToCheck.getReleaseDate().isAfter(MIN_FILM_DATE) &&
                filmToCheck.getDuration().isPositive());
    }
}

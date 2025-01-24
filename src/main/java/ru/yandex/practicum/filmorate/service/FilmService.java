package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Likes.LikeRepository;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeRepository likeRepository;

    @Autowired
    public FilmService(@Qualifier("filmDb") FilmStorage filmStorage, @Qualifier("dbUser") UserStorage userStorage, LikeRepository likeRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeRepository = likeRepository;
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
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильма нет в хранилище"));
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film userLikedFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден в БД"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден в БД"));
        return likeRepository.likeFilm(userId, filmId)
                .orElseThrow(() -> new NotFoundException("При попытке поставить лайк фильму произошла ошибка"));
    }

    public Film userUnlikeFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден в БД"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден в БД"));
        return likeRepository.unlikeFilm(userId, filmId)
                .orElseThrow(() -> new NotFoundException("При снятии лайка с фильма произошла ошибка"));
    }

    public List<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество выбранных фильмов должно быть больше 0");
        }
        return likeRepository.getMostPopularFilms(count);
    }

    private boolean isFilmValid(Film filmToCheck) {
        return ((filmToCheck.getDescription() == null || filmToCheck.getDescription().length() < 200) &&
                filmToCheck.getReleaseDate().isAfter(MIN_FILM_DATE) &&
                filmToCheck.getDuration().isPositive());
    }
}

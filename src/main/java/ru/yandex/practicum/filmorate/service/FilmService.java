package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.HelperMethods;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeRepository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;


@Service
@Slf4j
public class FilmService {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeRepository likeRepository;
    private final JdbcTemplate jdbc;
    private final HelperMethods helperMethods;

    @Autowired
    public FilmService(@Qualifier("filmDb") FilmStorage filmStorage, @Qualifier("dbUser") UserStorage userStorage, LikeRepository likeRepository, JdbcTemplate jdbc, HelperMethods helperMethods) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeRepository = likeRepository;
        this.jdbc = jdbc;
        this.helperMethods = helperMethods;
    }

    public Film addFilm(Film film) {
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setGenres(helperMethods.checkGenreAndMpa(film));
        loadGenresAndLikedUsers(List.of(film));
        Film newFilm = filmStorage.addFilm(film);
        newFilm.setDuration(Duration.ofMinutes(newFilm.getDuration().toSeconds()));
        helperMethods.insertFilmGenres(newFilm);
        List<Genre> genres = helperMethods.getFilmGenres(newFilm);
        newFilm.setGenres(genres);
        MpaRating filmRating = helperMethods.getMpaRatingByFilm(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг МРА не найден в БД"));
        newFilm.setMpa(filmRating);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        if (!isFilmValid(film)) {
            log.error("Неверно указан один из параметров фильма: {}", film);
            throw new ValidationException("Неверно указан один из параметров фильма");
        }
        film.setGenres(helperMethods.checkGenreAndMpa(film));
        Film updatedFilm = filmStorage.updateFilm(film);
        helperMethods.insertFilmGenres(updatedFilm);
        updatedFilm.setGenres(new ArrayList<>());
        MpaRating mpa = helperMethods.getMpaRatingByFilm(updatedFilm.getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг МРА не найден в БД"));
        updatedFilm.setMpa(mpa);
        updatedFilm.setDuration(Duration.ofMinutes(updatedFilm.getDuration().toSeconds()));
        loadGenresAndLikedUsers(List.of(updatedFilm));
        return updatedFilm;
    }

    public Film getFilmById(int filmId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильма нет в хранилище"));
        loadGenresAndLikedUsers(List.of(film));
        return film;
    }

    public Collection<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        loadGenresAndLikedUsers(films);
        return films;
    }

    public Film userLikedFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден в БД"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден в БД"));
        Film likedFilm = likeRepository.likeFilm(userId, filmId)
                .orElseThrow(() -> new NotFoundException("При попытке поставить лайк фильму произошла ошибка"));
        loadGenresAndLikedUsers(List.of(likedFilm));
        return likedFilm;
    }

    public Film userUnlikeFilm(int filmId, int userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден в БД"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден в БД"));
        Film unlikedFilm = likeRepository.unlikeFilm(userId, filmId)
                .orElseThrow(() -> new NotFoundException("При снятии лайка с фильма произошла ошибка"));
        loadGenresAndLikedUsers(List.of(unlikedFilm));
        return unlikedFilm;
    }

    public List<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество выбранных фильмов должно быть больше 0");
        }
        List<Film> mostPopularFilms = likeRepository.getMostPopularFilms(count);
        loadGenresAndLikedUsers(mostPopularFilms);
        return mostPopularFilms;
    }

    private boolean isFilmValid(Film filmToCheck) {
        return ((filmToCheck.getDescription() == null || filmToCheck.getDescription().length() < 200) &&
                filmToCheck.getReleaseDate().isAfter(MIN_FILM_DATE) &&
                filmToCheck.getDuration().isPositive());
    }

    public void loadGenresAndLikedUsers(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from GENRE g, FILMGENRES fg where fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID in (" + inSql + ")";
        jdbc.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.addGenre(makeGenre(rs));
        }, films.stream().map(Film::getId).toArray());

        final String sqlQueryLikes = "SELECT * FROM \"User\" u, Likes l where l.user_id = u.user_id AND l.film_id in (" + inSql + ")";
        jdbc.query(sqlQueryLikes, (rs) -> {
            final Film film = filmById.get(rs.getInt("FILM_ID"));
            film.addLike(makeUser(rs));
        }, films.stream().map(Film::getId).toArray());
    }


    static Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getShort("genre_id"),
                rs.getString("genre_name"));
    }

    static User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("user_email"),
                rs.getString("user_login"),
                rs.getString("user_name"),
                rs.getDate("user_birthday").toLocalDate(),
                new ArrayList<>());
    }

}

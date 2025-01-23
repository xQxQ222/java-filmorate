package ru.yandex.practicum.filmorate.storage.Likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeDbRepository implements LikeRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmMapper filmMapper;
    private final UserMapper userMapper;
    private final GenreMapper genreMapper;

    @Override
    public Optional<Film> likeFilm(int userId, int filmId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("film_id", filmId);
        final String query = "INSERT INTO Likes VALUES (:user_id, :film_id)";
        jdbc.update(query, parameterSource, keyHolder);
        final String queryF = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION,f.MPA_RATING_ID, mr.rating_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id" +
                " WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSources = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<Film> films = jdbc.query(queryF, parameterSources, filmMapper).stream()
                .peek(film -> film.setUserLiked(getUserLiked(filmId)))
                .peek(film -> film.setGenres(getFilmGenres(film.getId())))
                .toList();
        return films.isEmpty() ? Optional.empty() : Optional.ofNullable(films.getFirst());
    }

    @Override
    public Optional<Film> unlikeFilm(int userId, int filmId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("film_id", filmId);
        final String query = "DELETE FROM Likes WHERE user_id = :user_id AND film_id = :film_id";
        jdbc.update(query, parameterSource, keyHolder);
        final String queryF = "SELECT f.film_id, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION,f.MPA_RATING_ID, mr.rating_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id" +
                " WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSources = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<Film> films = jdbc.query(queryF, parameterSources, filmMapper).stream()
                .peek(film -> film.setUserLiked(getUserLiked(film.getId())))
                .peek(film -> film.setGenres(getFilmGenres(film.getId())))
                .toList();
        return films.isEmpty() ? Optional.empty() : Optional.ofNullable(films.getFirst());
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        final String query = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION,f.MPA_RATING_ID, mr.rating_name, COUNT(l.USER_ID) likes_count\n" +
                "FROM Film f \n" +
                "JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id \n" +
                "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID \n" +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT :count";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("count", count);
        return jdbc.query(query, parameterSource, filmMapper).stream()
                .peek(film -> film.setUserLiked(getUserLiked(film.getId())))
                .peek(film -> film.setGenres(getFilmGenres(film.getId())))
                .toList();
    }

    private List<User> getUserLiked(int filmId) {
        final String query = "SELECT * FROM LIKES l LEFT JOIN \"User\" u ON u.user_id = l.user_id WHERE l.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        return jdbc.query(query, parameterSource, userMapper);
    }

    private List<Genre> getFilmGenres(int filmId) {
        final String query = "SELECT * FROM FilmGenres fg JOIN GENRE g ON fg.genre_id=g.genre_id WHERE fg.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        return jdbc.query(query, parameterSource, genreMapper);
    }
}

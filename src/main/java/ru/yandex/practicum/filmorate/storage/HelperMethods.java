package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HelperMethods {

    private final NamedParameterJdbcOperations jdbc;
    private final GenreMapper genreMapper;
    private final MpaRatingMapper mpaRatingMapper;
    private final UserMapper userMapper;

    public List<Genre> getFilmGenres(Film film) {
        final String query = "SELECT * FROM FilmGenres fg JOIN GENRE g ON fg.genre_id=g.genre_id WHERE fg.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        return jdbc.query(query, parameterSource, genreMapper).stream()
                .peek(genre -> getGenreById(genre.getId()).orElseThrow(() -> new ValidationException("Жанр не найден в БД")))
                .toList();
    }

    public Optional<MpaRating> getMpaRatingByFilm(int filmId) {
        final String query = "SELECT * FROM FILM f JOIN MpaRating m ON f.mpa_rating_id = m.rating_id WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<MpaRating> rating = jdbc.query(query, parameterSource, mpaRatingMapper);
        return rating.isEmpty() ? Optional.empty() : Optional.ofNullable(rating.getFirst());
    }

    public void insertFilmGenres(Film newFilm) {
        if (!newFilm.getGenres().isEmpty()) {
            StringBuilder insertFilmGenres = new StringBuilder("INSERT INTO FilmGenres VALUES");
            for (Genre genre : newFilm.getGenres()) {
                insertFilmGenres.append(" (" + newFilm.getId() + ", " + genre.getId() + "),");
            }
            insertFilmGenres.delete(insertFilmGenres.lastIndexOf(","), insertFilmGenres.length());
            jdbc.update(insertFilmGenres.toString(), new MapSqlParameterSource());
        }
    }

    public Optional<Genre> getGenreById(short genreId) {
        final String query = "SELECT * FROM Genre WHERE genre_id = :genre_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("genre_id", genreId);
        List<Genre> genre = jdbc.query(query, parameterSource, genreMapper);
        return genre.isEmpty() ? Optional.empty() : Optional.ofNullable(genre.getFirst());
    }

    public List<User> getUserLiked(int filmId) {
        final String query = "SELECT * FROM LIKES l LEFT JOIN \"User\" u ON u.user_id = l.user_id WHERE l.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        return jdbc.query(query, parameterSource, userMapper);
    }

}

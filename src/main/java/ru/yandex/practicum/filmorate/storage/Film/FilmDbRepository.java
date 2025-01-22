package ru.yandex.practicum.filmorate.storage.Film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("filmDb")
@RequiredArgsConstructor
public class FilmDbRepository implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmMapper filmMapper;

    @Override
    public Collection<Film> getFilms() {
        final String query = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION,f.GENRE_ID,f.MPA_RATING_ID, mr.rating_name, g.genre_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id" +
                " JOIN GENRE g ON f.GENRE_ID = g.Genre_id";
        return jdbc.query(query, filmMapper);
    }

    @Override
    public Film updateFilm(Film film) {
        final String updateRequest = "UPDATE Film SET film_name = :film_name, film_description = :film_description, film_release_date = :film_release_date," +
                " film_duration = :film_duration, genre_id = :genre_id, mpa_rating_id = :mpa_rating_id WHERE film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("film_name", film.getName())
                .addValue("film_description", film.getDescription())
                .addValue("film_release_date", film.getReleaseDate())
                .addValue("film_duration", film.getDuration().toMinutes())
                .addValue("genre_id", film.getGenre().getId())
                .addValue("mpa_rating_id", film.getMpaRating().getId());

        int rowsUpdated = jdbc.update(updateRequest, parameterSource);
        if (rowsUpdated < 1) {
            throw new NotFoundException("Фильм не обновлен, т.к. не найден в базе данных");
        }
        return film;
    }

    @Override
    public Film addFilm(Film newFilm) {
        final String query = "INSERT INTO FILM (film_name, film_description, film_release_date, film_duration, genre_id, mpa_rating_id)" +
                " VALUES (:film_name, :film_description, :film_release_date, :film_duration, :genre_id, :mpa_rating_id)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_name", newFilm.getName())
                .addValue("film_description", newFilm.getDescription())
                .addValue("film_release_date", newFilm.getReleaseDate())
                .addValue("film_duration", newFilm.getDuration().toMinutes())
                .addValue("genre_id", newFilm.getGenre().getId())
                .addValue("mpa_rating_id", newFilm.getMpaRating().getId());

        jdbc.update(query, parameterSource, keyHolder, new String[]{"film_id"});
        newFilm.setId(keyHolder.getKeyAs(Integer.class));
        return newFilm;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        final String query = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION,f.GENRE_ID,f.MPA_RATING_ID, mr.rating_name, g.genre_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id" +
                " JOIN GENRE g ON f.GENRE_ID = g.Genre_id" +
                " WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<Film> films = jdbc.query(query, parameterSource, filmMapper);
        return films.isEmpty() ? Optional.empty() : Optional.ofNullable(films.getFirst());
    }
}

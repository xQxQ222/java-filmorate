package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("filmDb")
@RequiredArgsConstructor
public class FilmDbRepository implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmMapper filmMapper;

    @Override
    public List<Film> getFilms() {
        final String query = "select * from FILM f, MPARATING m where f.mpa_rating_id = m.rating_id";
        return jdbc.query(query, filmMapper);
    }

    @Override
    public Film updateFilm(Film film) {
        final String updateRequest = "UPDATE Film SET film_name = :film_name, film_description = :film_description, film_release_date = :film_release_date," +
                " film_duration = :film_duration, mpa_rating_id = :mpa_rating_id WHERE film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("film_name", film.getName())
                .addValue("film_description", film.getDescription())
                .addValue("film_release_date", film.getReleaseDate())
                .addValue("film_duration", film.getDuration().toSeconds())
                .addValue("mpa_rating_id", film.getMpa().getId());

        int rowsUpdated = jdbc.update(updateRequest, parameterSource);
        if (rowsUpdated < 1) {
            throw new NotFoundException("Фильм не обновлен, т.к. не найден в базе данных");
        }

        final String deleteRequest = "DELETE FROM FilmGenres WHERE film_id = :film_id";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        jdbc.update(deleteRequest, deleteParams);
        return film;
    }

    @Override
    public Film addFilm(Film newFilm) {
        final String query = "INSERT INTO FILM (film_name, film_description, film_release_date, film_duration, mpa_rating_id)" +
                " VALUES (:film_name, :film_description, :film_release_date, :film_duration, :mpa_rating_id)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_name", newFilm.getName())
                .addValue("film_description", newFilm.getDescription())
                .addValue("film_release_date", newFilm.getReleaseDate())
                .addValue("film_duration", newFilm.getDuration().toSeconds())
                .addValue("mpa_rating_id", newFilm.getMpa().getId());


        jdbc.update(query, parameterSource, keyHolder, new String[]{"film_id"});
        newFilm.setId(keyHolder.getKeyAs(Integer.class));
        return newFilm;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        final String query = "select * from FILM f, MPARATING m where f.mpa_rating_id = m.rating_id AND f.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<Film> films = jdbc.query(query, parameterSource, filmMapper);
        return films.isEmpty() ? Optional.empty() : Optional.ofNullable(films.getFirst());
    }

}

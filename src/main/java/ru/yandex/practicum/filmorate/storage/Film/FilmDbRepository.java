package ru.yandex.practicum.filmorate.storage.Film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("filmDb")
@RequiredArgsConstructor
public class FilmDbRepository implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmMapper filmMapper;
    private final GenreMapper genreMapper;
    private final MpaRatingMapper mpaRatingMapper;

    @Override
    public Collection<Film> getFilms() {
        final String query = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION, f.MPA_RATING_ID, mr.rating_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id";
        return jdbc.query(query, filmMapper).stream()
                .peek(film -> film.setGenres(getFilmGenres(film)))
                .peek(film -> film.setMpa(getMpaRatingByFilm(film.getId()).orElseThrow(() -> new NotFoundException("Рейтинг МРА не найден в БД"))))
                .toList();
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
                .addValue("film_duration", film.getDuration().toMinutes())
                .addValue("mpa_rating_id", film.getMpa().getId());

        int rowsUpdated = jdbc.update(updateRequest, parameterSource);
        if (rowsUpdated < 1) {
            throw new NotFoundException("Фильм не обновлен, т.к. не найден в базе данных");
        }

        final String deleteRequest = "DELETE FROM FilmGenres WHERE film_id = :film_id";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        jdbc.update(deleteRequest, deleteParams);
        insertFilmGenres(film);
        MpaRating mpa = getMpaRatingByFilm(film.getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг МРА не найден в БД"));
        film.setMpa(mpa);
        film.setGenres(getFilmGenres(film));
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
        newFilm.setDuration(Duration.ofMinutes(newFilm.getDuration().toSeconds()));
        insertFilmGenres(newFilm);
        newFilm.setGenres(getFilmGenres(newFilm));
        MpaRating filmRating = getMpaRatingByFilm(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Не найден рейтинг МРА в БД"));
        newFilm.setMpa(filmRating);
        return newFilm;
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        final String query = "SELECT f.FILM_ID, f.FILM_DESCRIPTION,f.FILM_NAME,f.FILM_RELEASE_DATE,f.FILM_DURATION, f.MPA_RATING_ID, mr.rating_name" +
                " FROM Film f" +
                " JOIN MPARATING mr ON f.MPA_RATING_ID=mr.Rating_id" +
                " WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<Film> films = jdbc.query(query, parameterSource, filmMapper).stream()
                .peek(film -> film.setGenres(getFilmGenres(film)))
                .peek(film -> film.setMpa(getMpaRatingByFilm(filmId).orElseThrow(() -> new NotFoundException("Рейтинг МРА не найден в БД"))))
                .toList();
        return films.isEmpty() ? Optional.empty() : Optional.ofNullable(films.getFirst());
    }

    private List<Genre> getFilmGenres(Film film) {
        final String query = "SELECT * FROM FilmGenres fg JOIN GENRE g ON fg.genre_id=g.genre_id WHERE fg.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        return jdbc.query(query, parameterSource, genreMapper).stream()
                .peek(genre -> getGenreById(genre.getId()).orElseThrow(() -> new ValidationException("Жанр не найден в БД")))
                .toList();
    }

    private Optional<MpaRating> getMpaRatingByFilm(int filmId) {
        final String query = "SELECT * FROM FILM f JOIN MpaRating m ON f.mpa_rating_id = m.rating_id WHERE f.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", filmId);
        List<MpaRating> rating = jdbc.query(query, parameterSource, mpaRatingMapper);
        return rating.isEmpty() ? Optional.empty() : Optional.ofNullable(rating.getFirst());
    }

    private void insertFilmGenres(Film newFilm) {
        if (!newFilm.getGenres().isEmpty()) {
            StringBuilder insertFilmGenres = new StringBuilder("INSERT INTO FilmGenres VALUES");
            for (Genre genre : newFilm.getGenres()) {
                insertFilmGenres.append(" (" + newFilm.getId() + ", " + genre.getId() + "),");
            }
            insertFilmGenres.delete(insertFilmGenres.lastIndexOf(","), insertFilmGenres.length());
            jdbc.update(insertFilmGenres.toString(), new MapSqlParameterSource());
        }
    }

    private Optional<Genre> getGenreById(short genreId) {
        final String query = "SELECT * FROM Genre WHERE genre_id = :genre_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("genre_id", genreId);
        List<Genre> genre = jdbc.query(query, parameterSource, genreMapper);
        return genre.isEmpty() ? Optional.empty() : Optional.ofNullable(genre.getFirst());
    }
}

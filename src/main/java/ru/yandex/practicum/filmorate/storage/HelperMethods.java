package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRepository;

import java.util.List;
import java.util.Optional;

@Component
public class HelperMethods {

    private final NamedParameterJdbcOperations jdbc;
    private final GenreMapper genreMapper;
    private final MpaRatingMapper mpaRatingMapper;
    private final GenreRepository genreRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final List<Genre> allGenres;
    private final List<MpaRating> allMpaRates;

    public HelperMethods(NamedParameterJdbcOperations jdbc, GenreMapper genreMapper, MpaRatingMapper mpaRatingMapper, GenreRepository genreRepository, MpaRatingRepository mpaRatingRepository) {
        this.jdbc = jdbc;
        this.genreMapper = genreMapper;
        this.mpaRatingMapper = mpaRatingMapper;
        this.genreRepository = genreRepository;
        this.mpaRatingRepository = mpaRatingRepository;
        allGenres = genreRepository.getAllGenres();
        allMpaRates = mpaRatingRepository.getAllRatings();
    }

    public List<Genre> getFilmGenres(Film film) {
        final String query = "SELECT * FROM FilmGenres fg JOIN GENRE g ON fg.genre_id=g.genre_id WHERE fg.film_id = :film_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        return jdbc.query(query, parameterSource, genreMapper);
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


    public List<Genre> checkGenreAndMpa(Film film) {
        if (!allMpaRates.contains(film.getMpa())) {
            throw new NotFoundException("МРА рейтинг не найден в БД");
        }
        List<Genre> filmGenres = film.getGenres();
        for (Genre genre : filmGenres) {
            if (!allGenres.contains(genre)) {
                throw new NotFoundException("Жанр не найден в БД");
            }
        }
        return filmGenres.stream().distinct().toList();
    }
}

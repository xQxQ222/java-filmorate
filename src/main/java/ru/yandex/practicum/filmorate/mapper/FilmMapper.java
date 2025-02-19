package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(rs.getInt("film_duration")));
        final String genresQuery = "SELECT g.genre_id, g.genre_name FROM Genre g " +
                "JOIN Film_Genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getShort("mpa_rating_id"));
        mpaRating.setName(rs.getString("rating_name"));
        film.setMpa(mpaRating);
        return film;
    }
}

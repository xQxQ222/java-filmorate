package ru.yandex.practicum.filmorate.storage.Genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Qualifier("genreDb")
public class GenreDbRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final GenreMapper genreMapper;

    @Override
    public Collection<Genre> getAllGenres() {
        final String query = "SELECT * FROM Genre";
        return jdbc.query(query, genreMapper);
    }

    @Override
    public Optional<Genre> getGenreById(short genreId) {
        final String query = "SELECT * FROM Genre WHERE genre_id = :genre_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("genre_id", genreId);
        List<Genre> genre = jdbc.query(query, parameterSource, genreMapper);
        return genre.isEmpty() ? Optional.empty() : Optional.ofNullable(genre.getFirst());
    }
}

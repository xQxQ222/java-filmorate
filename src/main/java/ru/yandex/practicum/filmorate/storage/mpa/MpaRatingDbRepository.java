package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("ratingDb")
@RequiredArgsConstructor
public class MpaRatingDbRepository implements MpaRatingRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final MpaRatingMapper ratingMapper;

    @Override
    public List<MpaRating> getAllRatings() {
        final String query = "SELECT * FROM MpaRating";
        return jdbc.query(query, ratingMapper);
    }

    @Override
    public Optional<MpaRating> getRatingById(short ratingId) {
        final String query = "SELECT * FROM MpaRating WHERE rating_id = :rating_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("rating_id", ratingId);
        List<MpaRating> ratings = jdbc.query(query, parameterSource, ratingMapper);
        return ratings.isEmpty() ? Optional.empty() : Optional.ofNullable(ratings.getFirst());
    }
}

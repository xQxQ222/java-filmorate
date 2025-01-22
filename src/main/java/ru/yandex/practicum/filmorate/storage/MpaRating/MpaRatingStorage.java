package ru.yandex.practicum.filmorate.storage.MpaRating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;


public interface MpaRatingStorage {
    Collection<MpaRating> getAllRatings();

    Optional<MpaRating> getRatingById(short ratingId);
}

package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    Optional<Film> likeFilm(int userId, int filmId);

    Optional<Film> unlikeFilm(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);
}

package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(short genreId);
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getGenres() {
        return genreRepository.getAllGenres();
    }

    public Genre getGenreById(short genreId) {
        return genreRepository.getGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр не найден в базе данных"));
    }
}

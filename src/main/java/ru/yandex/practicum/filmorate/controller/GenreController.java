package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Пришел Get запрос /genres");
        Collection<Genre> genres = genreService.getGenres();
        log.info("Отправлен ответ GET /genres с телом: {}", genres);
        return genres;
    }

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable short genreId) {
        log.info("Пришел Get запрос /genres/{}", genreId);
        Genre genre = genreService.getGenreById(genreId);
        log.info("Отправлен ответ GET /genres/{} с телом: {}", genreId, genre);
        return genre;
    }

}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        log.info("Пришел Get запрос /mpa");
        Collection<MpaRating> mpaRatings = mpaRatingService.getMpaRatings();
        log.info("Отправлен ответ GET /mpa с телом: {}", mpaRatings);
        return mpaRatings;
    }

    @GetMapping("/{mpaId}")
    public MpaRating getMpaRatingById(@PathVariable short mpaId) {
        log.info("Пришел Get запрос /mpa/{}", mpaId);
        MpaRating mpaRating = mpaRatingService.getMpaRatingById(mpaId);
        log.info("Отправлен ответ GET /mpa/{} с телом: {}", mpaId, mpaRating);
        return mpaRating;
    }
}

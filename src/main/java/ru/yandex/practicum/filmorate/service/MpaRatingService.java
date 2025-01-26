package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingRepository mpaRatingRepository;

    public Collection<MpaRating> getMpaRatings() {
        return mpaRatingRepository.getAllRatings();
    }

    public MpaRating getMpaRatingById(short ratingId) {
        return mpaRatingRepository.getRatingById(ratingId)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден в базе данных"));
    }
}

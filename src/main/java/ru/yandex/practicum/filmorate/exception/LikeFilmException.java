package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

public class LikeFilmException extends RuntimeException {
    @Getter
    private int userId;

    @Getter
    private int filmId;

    public LikeFilmException(String message, int userId, int filmId) {
        super(message);
        this.userId = userId;
        this.filmId = filmId;
    }
}

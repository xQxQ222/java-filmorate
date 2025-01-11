package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LikeFilmException extends RuntimeException {
    @Getter
    private final int userId;

    @Getter
    private final int filmId;

    public LikeFilmException(String message, int userId, int filmId) {
        super(message);
        this.userId = userId;
        this.filmId = filmId;
    }
}

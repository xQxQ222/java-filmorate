package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class EqualIdsException extends RuntimeException {
    @Getter
    private final int userId;

    public EqualIdsException(String message, int userId) {
        super(message);
        this.userId = userId;
    }
}

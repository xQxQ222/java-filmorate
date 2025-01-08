package ru.yandex.practicum.filmorate.exception.handle;

import lombok.Getter;

public class ExceptionResponce {
    @Getter
    private final String error;
    @Getter
    private final String description;

    public ExceptionResponce(String error, String description) {
        this.error = error;
        this.description = description;
    }
}

package ru.yandex.practicum.filmorate.exception.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.LikeFilmException;
import ru.yandex.practicum.filmorate.exception.FriendException;
import ru.yandex.practicum.filmorate.exception.EqualIdsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ExceptionResponce handleAlreadyFriend(final FriendException e) {
        log.error("Ошибка при добавлении / удалении друга (id: {}), id пользователя: {}", e.getFriendId(), e.getUserId());
        return new ExceptionResponce("Ошибка при добавлении / удалении друга", e.getMessage());
    }

    @ExceptionHandler
    public ExceptionResponce handleEqualUserId(final EqualIdsException e) {
        log.error("Сделан запрос на список друзей с одинаковым id: {}", e.getUserId());
        return new ExceptionResponce("Сделан запрос на список друзей с одинаковым id", e.getMessage());
    }

    @ExceptionHandler
    public ExceptionResponce handleNotFound(final NotFoundException e) {
        log.error(e.getMessage());
        return new ExceptionResponce("Объект не найден в хранилище", e.getMessage());
    }

    @ExceptionHandler
    public ExceptionResponce handleLikedFilm(final LikeFilmException e) {
        log.error("Ошибка при попытке лайкнуть / убрать лайк с фильма (id фильма: {}, id пользователя: {})", e.getFilmId(), e.getUserId());
        return new ExceptionResponce("Ошибка при попытке лайкнуть / убрать лайк с фильма", e.getMessage());
    }
}
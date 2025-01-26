package ru.yandex.practicum.filmorate.exception.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponce handleAlreadyFriend(final FriendException e) {
        log.error("Ошибка при добавлении / удалении друга (id: {}), id пользователя: {}", e.getFriendId(), e.getUserId());
        return new ExceptionResponce("Ошибка при добавлении / удалении друга", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponce handleEqualUserId(final EqualIdsException e) {
        log.error("Сделан запрос на список друзей с одинаковым id: {}", e.getUserId());
        return new ExceptionResponce("Сделан запрос на список друзей с одинаковым id", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponce handleNotFound(final NotFoundException e) {
        log.error(e.getMessage());
        return new ExceptionResponce("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponce handleLikedFilm(final LikeFilmException e) {
        log.error("Ошибка при попытке лайкнуть / убрать лайк с фильма (id фильма: {}, id пользователя: {})", e.getFilmId(), e.getUserId());
        return new ExceptionResponce("Ошибка при попытке лайкнуть / убрать лайк с фильма", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponce handleValidate(final ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage());
        return new ExceptionResponce("Произошла ошибка валидации", e.getMessage());
    }
}

package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class FriendException extends RuntimeException {
    @Getter
    private final int userId;
    @Getter
    private final int friendId;

    public FriendException(String message, int userId, int friendId) {
        super(message);
        this.userId = userId;
        this.friendId = friendId;
    }

}

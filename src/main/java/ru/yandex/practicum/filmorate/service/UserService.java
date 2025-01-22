package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendException;
import ru.yandex.practicum.filmorate.exception.EqualIdsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier("dbUser") UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public User addUser(User user) {
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя: {}", user);
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя: {}", user);
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        return userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя нет в базе данных"));
    }

    public User addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new EqualIdsException("Пользователь не может добавить сам себя в друзья", userId);
        }
        return friendsStorage.beFriend(userId, friendId)
                .orElseThrow(() -> new NotFoundException("Произошла ошибка при добавлении в друзья"));
    }

    public User deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new EqualIdsException("Пользователь не может удалить сам себя из друзей", userId);
        }
        User user = friendsStorage.unfriendUser(userId, friendId)
                .orElseThrow(() -> new NotFoundException("Пользователя нет в базе данных"));
        return user;
    }

    public List<User> getUserFriends(int userId) {
        return friendsStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (userId == otherId) {
            throw new EqualIdsException("Id пользователя совпадает с другим id", userId);
        }
        return friendsStorage.getCommonFriend(userId, otherId);
    }

    private boolean isUserValid(User userToCheck) {
        return (!userToCheck.getLogin().contains(" ") &&
                userToCheck.getBirthday().isBefore(LocalDate.now()));
    }
}

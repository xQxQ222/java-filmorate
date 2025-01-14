package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendException;
import ru.yandex.practicum.filmorate.exception.EqualIdsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
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
        return userStorage.getUserById(userId);
    }

    public User addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new EqualIdsException("Пользователь не может добавить сам себя в друзья", userId);
        }
        User user = userStorage.getUserById(userId);
        User newFriend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            throw new FriendException("Данные пользователи уже друзья", userId, friendId);
        }
        user.getFriends().add(friendId);
        newFriend.getFriends().add(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new EqualIdsException("Пользователь не может удалить сам себя из друзей", userId);
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> getUserFriends(int userId) {
        Set<Integer> userFriendsId = userStorage.getUserById(userId).getFriends();
        return userFriendsId.stream()
                .map(userStorage::getUserById).toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (userId == otherId) {
            throw new EqualIdsException("Id пользователя совпадает с другим id", userId);
        }
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> friendFriends = userStorage.getUserById(otherId).getFriends();
        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    private boolean isUserValid(User userToCheck) {
        return (!userToCheck.getLogin().contains(" ") &&
                userToCheck.getBirthday().isBefore(LocalDate.now()));
    }
}

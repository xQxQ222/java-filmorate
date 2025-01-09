package ru.yandex.practicum.filmorate.storage.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int usersCounter = 0;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя: {}", user);
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя заменено на значение логина: {}", user.getLogin());
        }
        user.setId(getNextId());
        log.debug("Получен новый id пользователя: {}", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            log.error("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя: {}", user);
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя заменено на значение логина: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return users.get(userId);
    }

    private boolean isUserValid(User userToCheck) {
        return (!userToCheck.getLogin().contains(" ") &&
                userToCheck.getBirthday().isBefore(LocalDate.now()));
    }

    private int getNextId() {
        return ++usersCounter;
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя", new ValidationException("Неверно указан один из параметров пользователя"));
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя заменено на значение логина: {}", user.getLogin());
        }
        user.setId(getNextId());
        log.debug("Получен новый id пользователя: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь с id {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователя с данным id не существует", new NotFoundException("Пользователя с данным id не существует"));
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        if (!isUserValid(user)) {
            log.error("Неверно указан один из параметров пользователя", new ValidationException("Неверно указан один из параметров пользователя"));
            throw new ValidationException("Неверно указан один из параметров пользователя");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя заменено на значение логина: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    private boolean isUserValid(User userToCheck) {
        return (!userToCheck.getLogin().contains(" ") &&
                userToCheck.getBirthday().isBefore(LocalDate.now()));
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

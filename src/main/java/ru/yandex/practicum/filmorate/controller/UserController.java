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
    private int usersCounter = 0;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел Get запрос /users");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("пришел Post запрос /users с телом: {}", user);
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
        log.info("Отправлен ответ Post /users с телом: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("пришел Put запрос /users с телом: {}", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с id = {} не существует", user.getId());
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
        log.info("Отправлен Put запрос /users с телом: {}", user);
        return user;
    }

    private boolean isUserValid(User userToCheck) {
        return (!userToCheck.getLogin().contains(" ") &&
                userToCheck.getBirthday().isBefore(LocalDate.now()));
    }

    private int getNextId() {
        return ++usersCounter;
    }
}

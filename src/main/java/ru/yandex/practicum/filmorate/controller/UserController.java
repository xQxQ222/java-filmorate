package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.User.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел GET запрос /users");
        Collection<User> users = userStorage.getUsers();
        log.info("Отправлен ответ GET /users с телом: {}", users);
        return users;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable int userId) {
        log.info("Пришел GET запрос /users/{}", userId);
        List<User> userFriends = userService.getUserFriends(userId);
        log.info("Отправлен ответ GET /users/{} с телом: {}", userId, userFriends);
        return userFriends;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/friends/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Пришел GET запрос /user/{}/friends/{}", userId, otherId);
        List<User> commonFriends = userService.getCommonFriends(userId, otherId);
        log.info("Отправлен ответ GET /users/{}/friends/{} c телом: {}", userId, otherId, commonFriends);
        return commonFriends;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.info("пришел GET запрос /users/{}", userId);
        User user = userStorage.getUserById(userId);
        log.info("Отправлен ответ GET /users/{} с телом: {}", userId, user);
        return user;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("пришел POST запрос /users с телом: {}", user);
        User newUser = userStorage.addUser(user);
        log.info("Отправлен ответ Post /users с телом: {}", newUser);
        return newUser;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("пришел PUT запрос /users с телом: {}", user);
        User updatedUser = userStorage.updateUser(user);
        log.info("Отправлен Put запрос /users с телом: {}", updatedUser);
        return updatedUser;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Пришел PUT запрос /users/{}/friends/{}", userId, friendId);
        User userWithFriend = userService.addFriend(userId, friendId);
        log.info("Отправлен ответ PUT /users/{}/friends/{} с телом: {}", userId, friendId, userWithFriend);
        return userWithFriend;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("Пришел DELETE запрос /users/{}/friends/{}", userId, friendId);
        User userWithFriend = userService.deleteFriend(userId, friendId);
        log.info("Отправлен ответ DELETE /users/{}/friends/{} с телом: {}", userId, friendId, userWithFriend);
        return userWithFriend;
    }
}

package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTests {
    UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void checkGetFilms() {
        assertEquals(0, userController.findAll().size());
        User user = new User("test@yandex.ru", LocalDate.of(2000, 1, 1));
        user.setLogin("kopatych");
        User createdUser = userController.create(user);
        assertEquals(1, userController.findAll().size());
    }

    @Test
    public void checkFilmValidation() {
        User user = new User("test@yandex.ru", LocalDate.of(2000, 1, 1));
        user.setLogin("kopatych s probelom");
        assertThrows(ValidationException.class, () -> userController.create(user));
        user.setLogin("kopatych");
        assertDoesNotThrow(() -> userController.create(user));
        user.setBirthday(LocalDate.of(2025, 1, 1));
        assertThrows(ValidationException.class, () -> userController.create(user));
        user.setBirthday(LocalDate.of(2000, 1, 1));
        assertDoesNotThrow(() -> userController.create(user));
    }
}

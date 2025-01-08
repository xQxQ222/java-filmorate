package ru.yandex.practicum.filmorate.storage.User;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User addUser(User newUser);

    User updateUser(int userId);

    User getUserById(int userId);
}

package ru.yandex.practicum.filmorate.storage.Friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendsStorage {
    Optional<User> beFriend(int userId, int friendId);

    Optional<User> unfriendUser(int userId, int friendId);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriend(int userId, int otherId);
}

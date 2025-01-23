package ru.yandex.practicum.filmorate.storage.Friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository {
    Optional<User> beFriend(User user, User friend);

    Optional<User> unfriendUser(User user, User friend);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriend(int userId, int otherId);
}

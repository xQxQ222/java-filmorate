package ru.yandex.practicum.filmorate.storage.Friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FriendDbRepository implements FriendsRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final UserMapper userMapper;

    @Override
    public Optional<User> beFriend(User user, User friend) {
        final String query = "INSERT INTO UserFriends VALUES (:user_id, :friend_id)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("friend_id", friend.getId());
        jdbc.update(query, parameterSource);
        user.setFriends(getUserFriends(user.getId()));
        return Optional.of(user);
    }

    @Override
    public Optional<User> unfriendUser(User user, User friend) {
        final String deleteSql = "DELETE FROM UserFriends WHERE user_id = :user_id AND friend_id = :friend_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("friend_id", friend.getId());
        jdbc.update(deleteSql, parameterSource);
        user.setFriends(getUserFriends(user.getId()));
        return Optional.of(user);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        final String query = "SELECT * FROM \"User\" u WHERE u.user_id IN " +
                "(SELECT fr.friend_id FROM USERFRIENDS fr WHERE fr.user_id = :user_id)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbc.query(query, parameterSource, userMapper);
    }

    @Override
    public List<User> getCommonFriend(int userId, int otherId) {
        final String query = "SELECT * FROM \"User\" u WHERE u.user_id IN " +
                "(SELECT uf1.friend_id\n" +
                "FROM UserFriends uf1\n" +
                "JOIN UserFriends uf2 ON uf1.friend_id = uf2.friend_id\n" +
                "WHERE uf1.user_id = :user_id AND uf2.user_id = :other_id)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("other_id", otherId);
        return jdbc.query(query, parameterSource, userMapper);
    }
}

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
public class FriendDbRepository implements FriendsStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final UserMapper userMapper;

    @Override
    public Optional<User> beFriend(int userId, int friendId) {
        final String query = "INSERT INTO USERFRIENDS VALUES (:user_id,:friend_id,false);\n" +
                "UPDATE USERFRIENDS " +
                "SET ACCEPTED_REQUEST = TRUE " +
                "WHERE ((USER_ID = :user_id AND FRIEND_ID = :friend_id)\n" +
                "    OR " +
                "    (USER_ID = :friend_id AND FRIEND_ID = :user_id))" +
                "AND EXISTS (" +
                "    SELECT 1" +
                "    FROM USERFRIENDS" +
                "    WHERE (USER_ID = :user_id AND FRIEND_ID = :friend_id)" +
                "    OR (USER_ID = :friend_id AND FRIEND_ID = :user_id)" +
                "    HAVING COUNT(*) = 2);";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        jdbc.update(query, parameterSource);
        final String queryUser = "SELECT * FROM \"User\" WHERE user_id = :user_id";
        MapSqlParameterSource parameterSourceForGetUser = new MapSqlParameterSource()
                .addValue("user_id", userId);
        List<User> users = jdbc.query(queryUser, parameterSourceForGetUser, userMapper);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.getFirst());
    }

    @Override
    public Optional<User> unfriendUser(int userId, int friendId) {
        final String updateSql = "UPDATE UserFriends SET accepted_request = FALSE WHERE user_id = :friend_id AND friend_id = :user_id";
        final String deleteSql = "DELETE FROM UserFriends WHERE user_id = :user_id AND friend_id = :friend_id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", friendId)
                .addValue("friend_id", userId);
        jdbc.update(updateSql, parameterSource);
        jdbc.update(deleteSql, parameterSource);
        final String queryUser = "SELECT * FROM \"User\" WHERE user_id = :user_id";
        MapSqlParameterSource parameterSourceForGetUser = new MapSqlParameterSource()
                .addValue("user_id", userId);
        List<User> users = jdbc.query(queryUser, parameterSourceForGetUser, userMapper);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.getFirst());
    }

    @Override
    public List<User> getUserFriends(int userId) {
        final String query = "SELECT * FROM \"User\" u WHERE u.user_id IN " +
                "(SELECT fr.friend_id FROM USERFRIENDS fr WHERE fr.user_id = :user_id AND fr.ACCEPTED_REQUEST = TRUE)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbc.query(query, parameterSource, userMapper);
    }

    @Override
    public List<User> getCommonFriend(int userId, int otherId) {
        final String query = "SELECT * FROM \"User\" u \n" +
                "WHERE user_id IN (SELECT uf1.FRIEND_ID\n" +
                "FROM USERFRIENDS uf1\n" +
                "JOIN USERFRIENDS uf2\n" +
                "    ON uf1.FRIEND_ID = uf2.FRIEND_ID\n" +
                "WHERE uf1.USER_ID = :user_id AND uf2.USER_ID = :other_id AND uf1.ACCEPTED_REQUEST = TRUE AND uf2.ACCEPTED_REQUEST = TRUE)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("other_id", otherId);
        return jdbc.query(query, parameterSource, userMapper);
    }
}

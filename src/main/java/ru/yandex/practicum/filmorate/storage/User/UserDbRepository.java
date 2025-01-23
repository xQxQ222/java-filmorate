package ru.yandex.practicum.filmorate.storage.User;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Component
@Qualifier("dbUser")
@RequiredArgsConstructor
public class UserDbRepository implements UserStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final UserMapper userMapper;

    @Override
    public Collection<User> getUsers() {
        final String query = "SELECT * FROM \"User\"";
        return jdbc.query(query, userMapper).stream()
                .peek(user -> user.setFriends(getUserFriends(user.getId())))
                .toList();
    }

    @Override
    public User addUser(User newUser) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("user_email", newUser.getEmail());
        parameterSource.addValue("user_login", newUser.getLogin());
        if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        parameterSource.addValue("user_name", newUser.getName());
        parameterSource.addValue("user_birthday", newUser.getBirthday());
        jdbc.update("INSERT INTO \"User\" (user_email, user_login, user_name, user_birthday) " +
                        "VALUES (:user_email, :user_login, :user_name, :user_birthday)",
                parameterSource, keyHolder, new String[]{"user_id"});
        newUser.setId(keyHolder.getKeyAs(Integer.class));
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        final String updateRequest = "UPDATE \"User\" SET user_email = :user_email, user_login = :user_login, user_name = :user_name, user_birthday = :user_birthday WHERE user_id = :user_id";
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("user_email", user.getEmail())
                .addValue("user_login", user.getLogin())
                .addValue("user_name", user.getName())
                .addValue("user_birthday", user.getBirthday());

        int rowsUpdated = jdbc.update(updateRequest, parameterSource);
        if (rowsUpdated < 1) {
            throw new NotFoundException("Пользователь не обновлен, т.к. не найден в базе данных");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        final String query = "SELECT * FROM \"User\" WHERE user_id = :userId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("userId", userId);

        List<User> users = jdbc.query(query, parameterSource, userMapper);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.getFirst());
    }

    private List<User> getUserFriends(int userId) {
        final String query = "SELECT * FROM \"User\" u WHERE u.user_id IN " +
                "(SELECT fr.friend_id FROM USERFRIENDS fr WHERE fr.user_id = :user_id)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbc.query(query, parameterSource, userMapper);
    }
}

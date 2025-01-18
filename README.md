# java-filmorate
Template repository for Filmorate project.
Ссылка на ER-диаграмму: https://dbdiagram.io/d/678ae4d26b7fa355c3424421
Примеры запросов:
1)Топ фильмов по лайкам: "SELECT f.film_id, count(l.user_id) FROM Film f JOIN Likes l ON f.film_id=l.film_id GROUP BY l.film_id ORDER BY count(l.user_id) DESC LIMIT <Число фильмов>;"
2)Друзья пользователя: "SELECT uf.friend_id FROM UserFriend uf WHERE uf.user_id = <user_id> AND uf.accepted_request = true;"
3)Общие друзья: "SELECT u.user_id, u.user_name, u.user_email FROM User u JOIN UserFriends uf1 ON u.user_id = uf1.friend_id JOIN UserFriends uf2 ON u.user_id = uf2.friend_id WHERE uf1.user_id = <user_id_1> AND uf2.user_id = <user_id_2>;

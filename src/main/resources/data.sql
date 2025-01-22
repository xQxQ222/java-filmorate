INSERT INTO MpaRating (rating_name)
SELECT rating_name
FROM (VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17')
) AS new_ratings(rating_name)
WHERE NOT EXISTS (SELECT 1 FROM MpaRating WHERE MpaRating.rating_name = new_ratings.rating_name);

INSERT INTO Genre (genre_name)
SELECT genre_name
FROM (VALUES
    ('Боевик'),
    ('Вестерн'),
    ('Детектив'),
    ('Драма'),
    ('Исторический фильм'),
    ('Комедия'),
    ('Мелодрама'),
    ('Музыкальный фильм'),
    ('Нуар'),
    ('Приключенческий фильм'),
    ('Сказка'),
    ('Трагедия'),
    ('Трагикомедия'),
    ('Триллер'),
    ('Фантастический фильм'),
    ('Фильм ужасов'),
    ('Фильм-катастрофа')
) AS new_genres(genre_name)
WHERE NOT EXISTS (SELECT 1 FROM Genre WHERE Genre.genre_name = new_genres.genre_name);
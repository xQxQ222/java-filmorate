package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbRepository;
import ru.yandex.practicum.filmorate.storage.Genre.GenreDbRepository;
import ru.yandex.practicum.filmorate.storage.MpaRating.MpaRatingDbRepository;
import ru.yandex.practicum.filmorate.storage.User.UserDbRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserMapper.class, UserDbRepository.class, FilmMapper.class, GenreMapper.class, MpaRating.class, FilmDbRepository.class, MpaRatingDbRepository.class, GenreDbRepository.class, MpaRatingMapper.class})
class FilmoRateApplicationTests {
    private final UserDbRepository userStorage;
    private final FilmDbRepository filmDbRepository;
    private final MpaRatingDbRepository mpaRatingDbRepository;
    private final GenreDbRepository genreDbRepository;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isEmpty();

        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("testLogin");
        user.setName("testName");
        user.setBirthday(LocalDate.now());
        userStorage.addUser(user);
        Optional<User> userOptional2 = userStorage.getUserById(1);

        assertThat(userOptional2)
                .isPresent()
                .hasValueSatisfying(userF -> assertThat(userF).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void testFindFilmById(){
        Optional<Film> filmOptional = filmDbRepository.getFilmById(1);
        assertThat(filmOptional)
                .isEmpty();

        Film film = new Film();
        film.setName("Лара Крофт");
        film.setDescription("Крутой фильм");
        film.setDuration(Duration.ofMinutes(90));
        film.setReleaseDate(LocalDate.of(2019, Month.FEBRUARY,12));
        film.setMpa(mpaRatingDbRepository.getRatingById((short) 2).get());
        film.setGenres(genreDbRepository.getAllGenres().stream().toList());
        filmDbRepository.addFilm(film);

        Optional<Film> filmOptional2 = filmDbRepository.getFilmById(1);
        assertThat(filmOptional2)
                .isPresent()
                .hasValueSatisfying(film1 -> assertThat(film1).hasFieldOrPropertyWithValue("id", 1));
    }
}
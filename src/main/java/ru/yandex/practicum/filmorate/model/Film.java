package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    @JsonProperty
    private List<User> userLiked = new ArrayList<>();
    @JsonIgnore
    private long rate = 0;
    private MpaRating mpa;
    private List<Genre> genres = new ArrayList<>();

    public void addLike(User user) {
        userLiked.add(user);
        rate = userLiked.size();
    }

    public void removeLike(User user) {
        userLiked.remove(user);
        rate = userLiked.size();
    }
}

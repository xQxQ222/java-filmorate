package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@RequiredArgsConstructor
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
    @JsonIgnore
    private Set<Integer> userLiked = new HashSet<>();
    @JsonIgnore
    private long rate = 0;

    public void addLike(int userId) {
        userLiked.add(userId);
        rate = userLiked.size();
    }

    public void removeLike(int userId) {
        userLiked.remove(userId);
        rate = userLiked.size();
    }
}

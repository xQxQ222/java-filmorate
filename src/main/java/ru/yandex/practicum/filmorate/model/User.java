package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@NoArgsConstructor
public class User {
    private int id;
    @NonNull
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NonNull
    private LocalDate birthday;
    @JsonProperty
    private List<User> friends = new ArrayList<>();
}

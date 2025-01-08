package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@RequiredArgsConstructor
@ToString
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
    private Set<Integer> friends = new HashSet<>();
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.controller.marker.Marker;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"email", "login"})
public class User {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    private String email;
    @NotBlank(groups = Marker.OnCreate.class)
    @Pattern(regexp = "^\\S+$", groups = Marker.OnCreate.class)
    private String login;
    private String name;
    @Past(groups = Marker.OnCreate.class)
    private LocalDate birthday;
}

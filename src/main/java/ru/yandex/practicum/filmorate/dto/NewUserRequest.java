package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.marker.Marker;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(groups = Marker.OnCreate.class)
    @Pattern(regexp = "^\\S+$", groups = Marker.OnCreate.class)
    private String login;
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    private String email;
    @Past(groups = Marker.OnCreate.class)
    private LocalDate birthday;
}

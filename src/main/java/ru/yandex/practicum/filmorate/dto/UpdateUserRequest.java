package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.marker.Marker;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return ! (login == null || login.isEmpty());
    }

    public boolean hasBirthday() {
        return ! (birthday == null);
    }
}
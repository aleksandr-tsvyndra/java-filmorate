package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(regexp = "^[\\\\w-\\\\.]+@[\\\\w-]+(\\\\.[\\\\w-]+)*\\\\.[a-z]{2,}$",
            message = "Email не соответствует общепринятому формату")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[\\S]+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}

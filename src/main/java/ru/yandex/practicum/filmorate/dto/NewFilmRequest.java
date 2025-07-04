package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.controller.marker.Marker;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFilmRequest {
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @Size(max = 200, groups = Marker.OnCreate.class)
    private String description;
    @ReleaseDate(groups = Marker.OnCreate.class)
    private LocalDate releaseDate;
    @Positive(groups = Marker.OnCreate.class)
    private Integer duration;
    private Rating mpa;
    private Set<Genre> genres;
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.controller.marker.Marker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"name"})
public class Film {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @Size(max = 200, groups = Marker.OnCreate.class)
    private String description;
    @ReleaseDate(groups = Marker.OnCreate.class)
    private LocalDate releaseDate;
    @Positive(groups = Marker.OnCreate.class)
    private Integer duration;
    private final Set<Long> likes = new HashSet<>();
}

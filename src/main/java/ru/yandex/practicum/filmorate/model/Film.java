package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import ru.yandex.practicum.filmorate.controller.marker.Marker;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @Size(max = 200, groups = Marker.OnCreate.class)
    private String description;
    private LocalDate releaseDate;
    @Positive(groups = Marker.OnCreate.class)
    private Integer duration;
}

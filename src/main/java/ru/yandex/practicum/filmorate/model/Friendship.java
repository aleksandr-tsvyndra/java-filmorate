package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private Long id;
    private Long userId;      // ID пользователя, который отправил запрос на дружбу
    private Long friendId;
}

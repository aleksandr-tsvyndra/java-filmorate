package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.controller.marker.Marker;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен http-запрос на получение списка всех юзеров");
        return users.values();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен http-запрос на создание юзера");
        if (hasIdenticalEmail(user)) {
            log.warn("Имейл {} уже занят другим юзером", user.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
        if (hasIdenticalLogin(user)) {
            log.warn("Логин {} уже занят другим юзером", user.getLogin());
            throw new ValidationException("Этот логин уже используется");
        }
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано — будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Новый юзер c id {} был добавлен в базу данных", user.getId());
        return user;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен http-запрос на обновление юзера");
        if (users.containsKey(newUser.getId())) {
            log.info("Юзер с id {} был найден в базе данных", newUser.getId());
            User oldUser = users.get(newUser.getId());
            updateUserFields(oldUser, newUser);
            return oldUser;
        }
        log.warn("Юзера с id {} нет в базе данных", newUser.getId());
        throw new ValidationException("Юзер с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean hasIdenticalEmail(User user) {
        log.info("Проверяем имейл из http-запроса на дубликат");
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
    }

    private boolean hasIdenticalLogin(User user) {
        log.info("Проверяем логин из http-запроса на дубликат");
        return users.values()
                .stream()
                .map(User::getLogin)
                .anyMatch(user.getLogin()::equals);
    }

    private boolean hasLoginSpaces(String login) {
        log.info("Проверяем, что логин юзера не содержит пробельных символов");
        boolean isSpace = false;
        for (int i = 0; i < login.length(); i++) {
            if (Character.isSpaceChar(login.charAt(i))) {
                isSpace = true;
                break;
            }
        }
        return isSpace;
    }

    private boolean isEmailValid(String email) {
        log.info("Проверяем, что имейл юзера соответствует нужному формату");
        return email.matches(".*");
    }

    private void updateUserFields(User oldUser, User newUser) {
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank() && isEmailValid(newUser.getEmail())) {
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                if (!hasIdenticalEmail(newUser)) {
                    log.info("Имейл юзера с id {} был обновлен", newUser.getId());
                    oldUser.setEmail(newUser.getEmail());
                }
            }
        }
        if (newUser.getLogin() != null && !newUser.getLogin().isEmpty() && !hasLoginSpaces(newUser.getLogin())) {
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                if (!hasIdenticalLogin(newUser)) {
                    log.info("Логин юзера с id {} был обновлен", newUser.getId());
                    oldUser.setLogin(newUser.getLogin());
                }
            }
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            log.info("Имя юзера с id {} было обновлено", newUser.getId());
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null && newUser.getBirthday().isBefore(LocalDate.now())) {
            log.info("Дата рождения юзера с id {} была обновлена", newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
        }
    }
}

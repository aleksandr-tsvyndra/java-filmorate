package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        log.info("Получен http-запрос на получение списка всех юзеров");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Получен http-запрос на создание юзера");
        if (hasDuplicateEmail(user)) {
            var message = String.format("Имейл %s уже занят другим юзером", user.getEmail());
            log.warn(message);
            throw new DuplicateEmailException(message);
        }
        if (hasDuplicateLogin(user)) {
            var message = String.format("Логин %s уже занят другим юзером", user.getLogin());
            log.warn(message);
            throw new DuplicateLoginException(message);
        }
        user.setId(getNextId());
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя пользователя не указано — будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Новый юзер c id {} был добавлен в базу данных", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Получен http-запрос на обновление юзера");
        if (users.containsKey(newUser.getId())) {
            log.info("Юзер с id {} был найден в базе данных", newUser.getId());
            User oldUser = users.get(newUser.getId());
            updateFields(oldUser, newUser);
            log.info("Успешно выполнен http-запрос на обновление юзера с id {}", newUser.getId());
            return oldUser;
        }
        var message = String.format("Юзера с id %d нет в базе данных", newUser.getId());
        log.warn(message);
        throw new UserNotFoundException(message);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean hasDuplicateEmail(User user) {
        log.info("Проверяем имейл из http-запроса на дубликат");
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
    }

    private boolean hasDuplicateLogin(User user) {
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

    private void updateFields(User oldUser, User newUser) {
        if (Objects.nonNull(newUser.getEmail()) && !newUser.getEmail().isBlank() && isEmailValid(newUser.getEmail())) {
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                if (!hasDuplicateEmail(newUser)) {
                    log.info("Имейл юзера с id {} был обновлен", newUser.getId());
                    oldUser.setEmail(newUser.getEmail());
                }
            }
        }
        if (Objects.nonNull(newUser.getLogin()) && !newUser.getLogin().isEmpty()
                && !hasLoginSpaces(newUser.getLogin())) {
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                if (!hasDuplicateLogin(newUser)) {
                    log.info("Логин юзера с id {} был обновлен", newUser.getId());
                    oldUser.setLogin(newUser.getLogin());
                }
            }
        }
        if (Objects.nonNull(newUser.getName()) && !newUser.getName().isBlank()) {
            log.info("Имя юзера с id {} было обновлено", newUser.getId());
            oldUser.setName(newUser.getName());
        }
        if (Objects.nonNull(newUser.getBirthday()) && newUser.getBirthday().isBefore(LocalDate.now())) {
            log.info("Дата рождения юзера с id {} была обновлена", newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
        }
    }
}

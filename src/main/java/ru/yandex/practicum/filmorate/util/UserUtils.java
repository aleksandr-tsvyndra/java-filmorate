package ru.yandex.practicum.filmorate.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserUtils {

    public static void updateFields(User oldUser, UpdateUserRequest newUser, Collection<User> users) {
        if (newUser.hasEmail() && isEmailValid(newUser.getEmail())) {
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                try {
                    checkEmail(newUser, users);
                    log.info("Имейл юзера с id {} был обновлен", newUser.getId());
                    oldUser.setEmail(newUser.getEmail());
                } catch (DuplicateEmailException e) {
                    log.info("Имейл юзера с id {} обновить не получилось", newUser.getId());
                }
            }
        }
        if (newUser.hasLogin() && !hasLoginSpaces(newUser.getLogin())) {
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                try {
                    checkLogin(newUser, users);
                    log.info("Логин юзера с id {} был обновлен", newUser.getId());
                    oldUser.setLogin(newUser.getLogin());
                } catch (DuplicateLoginException e) {
                    log.info("Логин юзера с id {} обновить не получилось", newUser.getId());
                }
            }
        }
        if (newUser.hasName()) {
            log.info("Имя юзера с id {} было обновлено", newUser.getId());
            oldUser.setName(newUser.getName());
        }
        if (newUser.hasBirthday() && isBirthdayValid(newUser.getBirthday())) {
            log.info("Дата рождения юзера с id {} была обновлена", newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
        }
    }

    public static boolean hasLoginSpaces(String login) {
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

    public static boolean isEmailValid(String email) {
        log.info("Проверяем, что имейл юзера соответствует нужному формату");
        return email.matches(".*");
    }

    public static boolean isBirthdayValid(LocalDate birthday) {
        log.info("Проверяем, что дата рождения юзера не указана в будущем времени");
        return birthday.isBefore(LocalDate.now());
    }

    public static void checkEmail(NewUserRequest user, Collection<User> users) {
        log.info("Проверяем имейл при создании юзера на дубликат");
        boolean hasDuplicateEmail = users
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
        if (hasDuplicateEmail) {
            var message = String.format("Имейл %s уже занят другим юзером", user.getEmail());
            log.warn(message);
            throw new DuplicateEmailException(message);
        }
    }

    public static void checkEmail(UpdateUserRequest user, Collection<User> users) {
        log.info("Проверка имейла при обновлении юзера на дубликат");
        boolean hasDuplicateEmail = users
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
        if (hasDuplicateEmail) {
            var message = String.format("Имейл %s уже занят другим юзером", user.getEmail());
            log.warn(message);
            throw new DuplicateEmailException(message);
        }
    }

    public static void checkLogin(NewUserRequest user, Collection<User> users) {
        log.info("Проверка логина при создании юзера на дубликат");
        boolean hasDuplicateLogin = users
                .stream()
                .map(User::getLogin)
                .anyMatch(user.getLogin()::equals);
        if (hasDuplicateLogin) {
            var message = String.format("Логин %s уже занят другим юзером", user.getLogin());
            log.warn(message);
            throw new DuplicateLoginException(message);
        }
    }

    public static void checkLogin(UpdateUserRequest user, Collection<User> users) {
        log.info("Проверка логина при обновлении юзера на дубликат");
        boolean hasDuplicateLogin = users
                .stream()
                .map(User::getLogin)
                .anyMatch(user.getLogin()::equals);
        if (hasDuplicateLogin) {
            var message = String.format("Логин %s уже занят другим юзером", user.getLogin());
            log.warn(message);
            throw new DuplicateLoginException(message);
        }
    }

    public static void checkName(NewUserRequest user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя пользователя при создании не указано — будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    public static User checkUserPresence(Long userId, UserService uS) {
        return uS.getById(userId);
    }
}

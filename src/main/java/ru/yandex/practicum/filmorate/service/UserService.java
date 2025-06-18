package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(long id) {
        User userById = userStorage.findById(id);
        if (Objects.isNull(userById)) {
            var message = String.format("Юзера с id %d нет в базе данных", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        log.info("Юзер с id {} успешно найден", id);
        return userById;
    }

    public Collection<User> getUserFriends(long id) {
        User user = getById(id);
        Set<Long> userFriends = user.getFriends();
        log.info("Выводим список друзей юзера с id {}", id);
        return userStorage.getAll()
                .stream()
                .filter(u -> userFriends.contains(u.getId()))
                .toList();
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        Collection<User> userFriends = getUserFriends(id);
        Collection<User> otherUserFriends = getUserFriends(otherId);
        log.info("Выводим список общих друзей юзеров с id {} и {}", id, otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .toList();
    }

    public User create(User user) {
        checkEmail(user);
        checkLogin(user);
        checkName(user);
        return userStorage.create(user);
    }

    public User update(User newUser) {
        User oldUser = getById(newUser.getId());
        log.info("Юзер с id {} был найден в базе данных", newUser.getId());
        updateFields(oldUser, newUser);
        log.info("Успешно выполнен http-запрос на обновление юзера с id {}", newUser.getId());
        return oldUser;
    }

    public User addFriend(long id, long friendId) {
        User user = getById(id);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Юзеры с id {} и id {} стали друзьями", id, friendId);
        return user;
    }

    public User removeFriend(long id, long friendId) {
        User user = getById(id);
        User friend = getById(friendId);
        boolean result = user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        if (result) {
            log.info("Юзеры с id {} и id {} перестали быть друзьями", id, friendId);
        } else {
            log.info("Юзеры с id {} и id {} изначально не были друзьями", id, friendId);
        }
        return user;
    }

    private boolean hasDuplicateEmail(User user) {
        log.info("Проверяем имейл из http-запроса на дубликат");
        return userStorage.getAll()
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
    }

    private boolean hasDuplicateLogin(User user) {
        log.info("Проверяем логин из http-запроса на дубликат");
        return userStorage.getAll()
                .stream()
                .map(User::getLogin)
                .anyMatch(user.getLogin()::equals);
    }

    private void checkEmail(User user) {
        if (hasDuplicateEmail(user)) {
            var message = String.format("Имейл %s уже занят другим юзером", user.getEmail());
            log.warn(message);
            throw new DuplicateEmailException(message);
        }
    }

    private void checkLogin(User user) {
        if (hasDuplicateLogin(user)) {
            var message = String.format("Логин %s уже занят другим юзером", user.getLogin());
            log.warn(message);
            throw new DuplicateLoginException(message);
        }
    }

    private void checkName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя пользователя не указано — будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
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

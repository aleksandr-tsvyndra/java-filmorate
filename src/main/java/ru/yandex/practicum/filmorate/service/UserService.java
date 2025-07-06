package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.DuplicateEmailException;
import ru.yandex.practicum.filmorate.exception.DuplicateKeyException;
import ru.yandex.practicum.filmorate.exception.DuplicateLoginException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public Collection<User> getAll() {
        return userStorage.getAll()
                .stream()
                .peek(u -> u.getFriends().addAll(setUserFriends(u.getId())))
                .toList();
    }

    public User getById(long id) {
        User userById = userStorage.findById(id);
        if (Objects.isNull(userById)) {
            var message = String.format("Юзера с id %d нет в базе данных", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        userById.getFriends().addAll(setUserFriends(userById.getId()));
        log.info("Юзер с id {} успешно найден", id);
        return userById;
    }

    public Collection<User> getUserFriends(long id) {
        checkUserPresence(id);
        log.info("Выводим список друзей юзера с id {}", id);
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        Collection<User> userFriends = getUserFriends(id);
        Collection<User> otherUserFriends = getUserFriends(otherId);
        log.info("Выводим список общих друзей юзеров с id {} и {}", id, otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .toList();
    }

    public User create(NewUserRequest request) {
        checkEmail(request.getEmail());
        checkLogin(request.getLogin());
        checkName(request);
        User user = UserMapper.mapToUser(request);
        return userStorage.create(user);
    }

    public User update(UpdateUserRequest request) {
        User user = checkUserPresence(request.getId());
        log.info("Юзер с id {} был найден в базе данных", request.getId());
        updateFields(user, request);
        userStorage.update(user);
        log.info("Успешно выполнен http-запрос на обновление юзера с id {}", user.getId());
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        checkUserPresence(userId);
        checkUserPresence(friendId);
        if (friendshipStorage.isFriend(userId, friendId)) {
            String message = String.format("Юзер с id %d уже в друзьях у юзера с id %d", friendId, userId);
            throw new DuplicateKeyException(message);
        }
        friendshipStorage.addFriend(userId, friendId);
        log.info("Юзер с id {} добавлен в друзья юзера с id {}", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        checkUserPresence(userId);
        checkUserPresence(friendId);
        friendshipStorage.removeFriend(userId, friendId);
        log.info("Юзер с id {} удален из друзей юзера с id {}", friendId, userId);
    }

    private Set<Long> setUserFriends(Long userId) {
        return userStorage.getUserFriends(userId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

    private User checkUserPresence(Long userId) {
        return getById(userId);
    }

    private void updateFields(User user, UpdateUserRequest request) {
        if (request.hasEmail() && isEmailValid(request.getEmail())) {
            if (!request.getEmail().equals(user.getEmail())) {
                try {
                    checkEmail(request.getEmail());
                    log.info("Имейл юзера с id {} был обновлен", request.getId());
                    user.setEmail(request.getEmail());
                } catch (DuplicateEmailException e) {
                    log.info("Имейл юзера с id {} обновить не получилось", request.getId());
                }
            }
        }
        if (request.hasLogin() && !hasLoginSpaces(request.getLogin())) {
            if (!request.getLogin().equals(user.getLogin())) {
                try {
                    checkLogin(request.getLogin());
                    log.info("Логин юзера с id {} был обновлен", request.getId());
                    user.setLogin(request.getLogin());
                } catch (DuplicateLoginException e) {
                    log.info("Логин юзера с id {} обновить не получилось", request.getId());
                }
            }
        }
        if (request.hasName()) {
            log.info("Имя юзера с id {} было обновлено", request.getId());
            user.setName(request.getName());
        }
        if (request.hasBirthday() && isBirthdayValid(request.getBirthday())) {
            log.info("Дата рождения юзера с id {} была обновлена", request.getId());
            user.setBirthday(request.getBirthday());
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

    private boolean isBirthdayValid(LocalDate birthday) {
        log.info("Проверяем, что дата рождения юзера не указана в будущем времени");
        return birthday.isBefore(LocalDate.now());
    }

    private void checkEmail(String email) {
        log.info("Проверяем имейл при создании юзера на дубликат");
        boolean hasDuplicateEmail = getAll()
                .stream()
                .map(User::getEmail)
                .anyMatch(email::equals);
        if (hasDuplicateEmail) {
            var message = String.format("Имейл %s уже занят другим юзером", email);
            log.warn(message);
            throw new DuplicateEmailException(message);
        }
    }

    private void checkLogin(String login) {
        log.info("Проверка логина при создании юзера на дубликат");
        boolean hasDuplicateLogin = getAll()
                .stream()
                .map(User::getLogin)
                .anyMatch(login::equals);
        if (hasDuplicateLogin) {
            var message = String.format("Логин %s уже занят другим юзером", login);
            log.warn(message);
            throw new DuplicateLoginException(message);
        }
    }

    private void checkName(NewUserRequest user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            log.info("Имя пользователя при создании не указано — будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.UserUtils;

import java.util.Collection;
import java.util.Objects;

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
        UserUtils.checkUserPresence(id, this);
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
        UserUtils.checkEmail(request, userStorage.getAll());
        UserUtils.checkLogin(request, userStorage.getAll());
        UserUtils.checkName(request);
        User user = UserMapper.mapToUser(request);
        return userStorage.create(user);
    }

    public User update(UpdateUserRequest request) {
        User oldUser = UserUtils.checkUserPresence(request.getId(), this);
        log.info("Юзер с id {} был найден в базе данных", request.getId());
        userStorage.update(oldUser, request);
        log.info("Успешно выполнен http-запрос на обновление юзера с id {}", oldUser.getId());
        return oldUser;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = UserUtils.checkUserPresence(userId, this);
        UserUtils.checkUserPresence(friendId, this);
        userStorage.addFriend(user, friendId);
        log.info("Юзер с id {} добавлен в друзья юзера с id {}", friendId, userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = UserUtils.checkUserPresence(userId, this);
        UserUtils.checkUserPresence(friendId, this);
        userStorage.removeFriend(user, friendId);
        log.info("Юзер с id {} удален из друзей юзера с id {}", friendId, userId);
    }
}

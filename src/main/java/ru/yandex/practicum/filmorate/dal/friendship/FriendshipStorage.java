package ru.yandex.practicum.filmorate.dal.friendship;

public interface FriendshipStorage {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    boolean isFriend(long userId, long friendId);
}
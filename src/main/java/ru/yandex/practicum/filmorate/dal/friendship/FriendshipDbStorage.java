package ru.yandex.practicum.filmorate.dal.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.dal.BaseStorage;

@Repository
public class FriendshipDbStorage extends BaseStorage<Friendship> implements FriendshipStorage {
    private static final String INSERT_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users WHERE user_id IN (SELECT f.user_id " +
            "FROM friends AS f WHERE f.user_id = ? AND f.friend_id = ?)";

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> friendshipMapper) {
        super(jdbc, friendshipMapper);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        insert(INSERT_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        delete(DELETE_QUERY, userId, friendId);
    }

    @Override
    public boolean isFriend(long userId, long friendId) {
        return findOne(FIND_FRIENDS_QUERY, userId, friendId).isPresent();
    }
}

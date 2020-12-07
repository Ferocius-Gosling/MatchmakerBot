package com.company;

import com.company.bot.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {
    private HashMap<Long, User> users;
    private ArrayList<Long> ids;
    private String hostname;
    private String dbLogin;
    private String dbPassword;
    private static final Logger logger = Logger.getLogger(TelegramBot.class.getName());

    public UserRepository(String hostname, String dbLogin, String dbPassword) {
        users = new HashMap<>();
        ids = new ArrayList<>();
        this.hostname = hostname;
        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
    }

    public User getUser(long id) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            var user = storage.loadUser(new User(id));
            User currentUserInQuestion = null;
            if (user == null)
                return null;
            if (user.getUserInQuestionId() != 0)
                currentUserInQuestion = storage.loadUser(new User(user.getUserInQuestionId()));
            var likedUsers = storage.getIdLikedUser("likes", user);
            for (long likedId : likedUsers) {
                var likedUser = storage.loadUser(new User(likedId));
                if (storage.getIdLikedUser("likes", likedUser).contains(user.getId())) {
                    likedUser.setUserInQuestion(user);
                    likedUser.addToWhoLikes();
                }
                user.setUserInQuestion(likedUser);
                user.addToWhoLikes(this);
            }
            var matchedUsers = storage.getIdLikedUser("matches", user);
            for (long matchedId : matchedUsers) {
                if (!user.getMatchedUsers().contains(storage.loadUser(new User(matchedId))))
                    user.addToMatchedUsers(storage.loadUser(new User(matchedId)));
            }
            if (user.getUserInQuestionId() != 0)
                user.setUserInQuestion(currentUserInQuestion);
            return user;
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }

    public User getNextUser(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            return storage.loadUser(user);
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }

    public void clearMatches(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            storage.deleteFromMatches(user);
        }
    }

    public void addUser(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            storage.deleteUser(user);
            storage.registerUser(user);
        }
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            if (!storage.isRowInLikesExist(userWhoLiked, userWhomLiked))
                storage.updateLikes(userWhoLiked, userWhomLiked);
        }
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            storage.updateMatches(userWhoLiked, userWhomLiked);
            storage.updateMatches(userWhomLiked, userWhoLiked);
            storage.deleteLikes(userWhoLiked, userWhomLiked);
            storage.deleteLikes(userWhomLiked, userWhoLiked);
        }
    }

    public void updateUserState(User user) throws SQLException, IOException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            storage.updateUser(user);
        }
    }

    public void updateUserLastFind(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage(hostname, dbLogin, dbPassword)) {
            storage.createConnection();
            storage.updateLastFind(user);
        }
    }
}

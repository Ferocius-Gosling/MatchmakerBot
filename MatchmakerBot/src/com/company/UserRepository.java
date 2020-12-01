package com.company;

import com.company.bot.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class UserRepository {
    private HashMap<Long, User> users;
    private ArrayList<Long> ids;

    public UserRepository() {
        users = new HashMap<>();
        ids = new ArrayList<>();
    }

    public User getUser(long id) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
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
            if (storage.getIdLikedUser("likes", likedUser).contains(user.getId())){
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
        e.printStackTrace();
        return null;
        }
    }

    public User getNextUser(User user) throws SQLException {
//        var random = new Random();
//        var next = random.nextInt(size());
//        var userToGet = users.get(ids.get(next));
//        if (size() == 1) return null;
//        var i = 0;
//        while (user.getId() == userToGet.getId() || !userToGet.isRegEnded()) {
//            next = random.nextInt(size());
//            userToGet = users.get(ids.get(next));
//            i++;
//            if (i > 10) return null;
//        }
//        return users.get(ids.get(next));
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            return storage.loadUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public void loadUsers() throws SQLException, ClassNotFoundException, IOException {
//        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
//            storage.createConnection();
//            users = storage.load();
//            for (User user : users.values()) {
//                var likedUsers = storage.getIdLikedUser("likes", user);
//                for (long id : likedUsers) {
//                    user.setUserInQuestion(users.get(id));
//                    user.addToWhoLikes(this);
//                }
//                var matchedUsers = storage.getIdLikedUser("matches", user);
//                for (long id : matchedUsers){
//                    if (!user.getMatchedUsers().contains(users.get(id)))
//                        user.addToMatchedUsers(users.get(id));
//                }
//            }
//            ids.addAll(users.keySet());
//        }
//    }

    public void clearMatches(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            storage.deleteFromMatches(user);
        }
    }

    public void addUser(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            storage.deleteUser(user);
            storage.registerUser(user);
        }
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            if (!storage.isRowInLikesExist(userWhoLiked, userWhomLiked))
                storage.updateLikes(userWhoLiked, userWhomLiked);
        }
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            storage.updateMatches(userWhoLiked, userWhomLiked);
            storage.updateMatches(userWhomLiked, userWhoLiked);
            storage.deleteLikes(userWhoLiked, userWhomLiked);
            storage.deleteLikes(userWhomLiked, userWhoLiked);
        }
    }

    public void updateUserState(User user) throws SQLException, IOException{
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            storage.updateUser(user);
        }
    }

    public void updateUserLastFind(User user) throws SQLException {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")) {
            storage.createConnection();
            storage.updateLastFind(user);
        }
    }
}

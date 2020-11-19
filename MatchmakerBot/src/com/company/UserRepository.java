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

    public int size() {
        return users.size();
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public User getNextUser(User user) {
        var random = new Random();
        var next = random.nextInt(size());
        var userToGet = users.get(ids.get(next));
        if (size() == 1) return null;
        var i = 0;
        while (user.getId() == userToGet.getId() || !userToGet.isRegEnded()){
            next = random.nextInt(size());
            userToGet = users.get(ids.get(next));
            i++;
            if (i > 10) return null;
        }
        return users.get(ids.get(next));
    }

    public void loadUsers() throws SQLException, ClassNotFoundException {
        try (SQLStorage storage = new SQLStorage("hostname","db-login", "db-password")) {
            storage.createConnection();
            users = storage.load();
            for (User user : users.values()){
                var likedUsers = storage.getIdLikedUser("likes", user);
                for (long id : likedUsers) {
                    user.setUserInQuestion(users.get(id));
                    user.addToWhoLikes();
                }
                var matchedUsers = storage.getIdLikedUser("matches", user);
                for (long id : matchedUsers)
                    user.addToMatchedUsers(users.get(id));
            }
            ids.addAll(users.keySet());
        }
    }
    // todo clear all catches
    public void clearMatches(User user) {
        try(SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")){
            storage.createConnection();
            storage.deleteFromMatches(user);
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        try(SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")){
            storage.createConnection();
            var id = user.getId();
            ids.add(id);
            users.put(id, user);
            storage.registerUser(user);
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) {
        try(SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")){
            storage.createConnection();
            storage.updateLikes(userWhoLiked, userWhomLiked);
        }
        catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked) {
        try (SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")){
            storage.createConnection();
            storage.updateMatches(userWhoLiked, userWhomLiked);
        }
        catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updateUserState(User user) {
        try(SQLStorage storage = new SQLStorage("hostname", "db-login", "db-password")){
            storage.createConnection();
            storage.updateUser(user);
        }
        catch (SQLException | ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
    }

}

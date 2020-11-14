package com.company;

import com.company.bot.User;

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

    public void loadUsers() {
        var storage = new SQLStorage();
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

    public void clearMatches(User user) {
        var storage = new SQLStorage();
        storage.deleteFromMatches(user);
        storage.close();
    }

    public void addUser(User user) {
        var storage = new SQLStorage();
        var id = user.getId();
        ids.add(id);
        users.put(id, user);
        storage.registerUser(user);
        storage.close();
    }

    public void updateLikes(User userWhoLiked, User userWhomLiked) {
        var storage = new SQLStorage();
        storage.updateLikes(userWhoLiked, userWhomLiked);
        storage.close();
    }

    public void updateMatches(User userWhoLiked, User userWhomLiked){
        var storage = new SQLStorage();
        storage.updateMatches(userWhoLiked, userWhomLiked);
        storage.close();
    }

    public void updateUserState(User user) {
        var storage = new SQLStorage();
        storage.updateUser(user);
        storage.close();
    }

}

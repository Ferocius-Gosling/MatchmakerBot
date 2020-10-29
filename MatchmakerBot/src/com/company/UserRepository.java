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

    public void addUser(User user) {
        var id = user.getId();
        ids.add(id);
        users.put(id, user);
    }


}

package com.company;

import java.util.*;

public class UserRepository {
    private HashMap<Long, User> users;

    public UserRepository() {
        users = new HashMap<>();
    }

    public int size() {
        return users.size();
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public User getNextUser() {
        var random = new Random();
        var next = random.nextInt(users.size());
        var ids = getIds();
        return users.get(ids.get(next));
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public ArrayList<Long> getIds() {
        return new ArrayList<Long>(users.keySet());
    }
}

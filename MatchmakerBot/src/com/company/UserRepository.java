package com.company;

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

    public User getNextUser() {
        var random = new Random();
        var next = random.nextInt(size());
        return users.get(ids.get(next));
    }

    public void addUser(User user) {
        var id = user.getId();
        ids.add(id);
        users.put(id, user);
    }


}

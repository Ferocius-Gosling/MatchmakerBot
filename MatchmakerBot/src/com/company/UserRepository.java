package com.company;

import java.util.*;

public class UserRepository {
    private ArrayList<User> userRepository;

    public UserRepository() {
        userRepository = new ArrayList<>();
    }

    public int size(){
        return userRepository.size();
    }

    public User getUser(int id) {
        return userRepository.get(id);
    }

    public User getNextUser(){
        var random = new Random();
        var next = random.nextInt(userRepository.size());
        return userRepository.get(next);
    }

    public void addUser(User user){
        userRepository.add(user);
    }
}

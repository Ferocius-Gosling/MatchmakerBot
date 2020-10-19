package com.company;

import java.io.File;
import java.util.ArrayList;

public class User {
    private final long id;
    private boolean isRegistred = true;
    private DialogStates state;
    private String name;
    private int age;
    private String city;
    private String info;
    private ArrayList<User> matchedUsers;
    private ArrayList<User> whoLikesThatUser;
    private File userPhoto;

    public User(long id) {
        whoLikesThatUser = new ArrayList<User>();
        matchedUsers = new ArrayList<User>();
        this.id = id;
        this.state = DialogStates.START;
    }

    public void addToWhoLikes(User user) {
        whoLikesThatUser.add(user);
        if (user.containsWhoLikesUser(this)) {
            addToMatchedUsers(user);
            user.addToMatchedUsers(this);
        }
    }

    public void addToMatchedUsers(User user) {
        matchedUsers.add(user);
    }

    public ArrayList<User> getMatchedUsers() {
        return matchedUsers;
    }

    public boolean containsWhoLikesUser(User user) {
        return whoLikesThatUser.contains(user);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setReg(boolean flag) {
        this.isRegistred = flag;
    }

    public void setUserPhoto(File photo) {
        this.userPhoto = photo;
    }

    public boolean isRegistred() {
        return this.isRegistred;
    }

    public DialogStates getCurrentState() {
        return this.state;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public String getCity() {
        return this.city;
    }

    public String getInfo() {
        return this.info;
    }

    public File getUserPhoto() {
        return this.userPhoto;
    }

    public long getId() {
        return this.id;
    }

    public void changeCurrentState(DialogStates nextState) {
        //mb validation
        this.state = nextState;
    }
}



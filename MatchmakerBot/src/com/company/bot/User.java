package com.company.bot;

import java.io.File;
import java.util.ArrayList;

public class User {
    private final long id;
    private boolean isRegistered = true;
    private boolean isRegEnded = false;
    private DialogState state;
    private String name;
    private int age;
    private String userName;
    private String city;
    private String info;
    private User userInQuestion;
    private ArrayList<User> matchedUsers;
    private ArrayList<User> whoLikesThatUser;
    private File userPhoto;

    public User(long id) {
        whoLikesThatUser = new ArrayList<User>();
        matchedUsers = new ArrayList<User>();
        this.id = id;
        this.state = DialogState.START;
    }

    public User(long id, String username, String name, int age,
                String city, String info, DialogState state, File userPhoto) {
        this.id = id;
        this.userName = username;
        this.name = name;
        this.city = city;
        this.age = age;
        this.info = info;
        this.state = state;
        switch (state)
        {
            case REG_AGE:
            case REG_NAME:
            case REG_CITY:
            case REG_INFO:
                isRegistered = false;
                isRegEnded = false;
            case START:
                isRegistered = true;
                isRegEnded = false;
            default:
                isRegEnded = true;
        }
        this.userPhoto = userPhoto;
        whoLikesThatUser = new ArrayList<User>();
        matchedUsers = new ArrayList<User>();
    }

    public void setUserInQuestion(User user) {
        userInQuestion = user;
    }

    public void clearMatched() {
        matchedUsers = new ArrayList<>();
    }

    public void addToWhoLikes() {
        if (!containsWhoLikesUser(userInQuestion)) {
            whoLikesThatUser.add(userInQuestion);
            if (userInQuestion.containsWhoLikesUser(this)) {
                addToMatchedUsers(userInQuestion);
                userInQuestion.addToMatchedUsers(this);
            }
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setReg(boolean flag) {
        this.isRegistered = flag;
        this.isRegEnded = flag;
    }

    public void setUserPhoto(File photo) {
        this.userPhoto = photo;
    }

    public boolean isRegEnded() {
        return this.isRegEnded;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    public DialogState getCurrentState() {
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

    public String getUserName() {
        return this.userName;
    }

    public File getUserPhoto() {
        return this.userPhoto;
    }

    public long getId() {
        return this.id;
    }

    public void changeCurrentState(DialogState nextState) {
        //mb validation
        this.state = nextState;
    }
}
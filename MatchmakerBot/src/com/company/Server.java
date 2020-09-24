package com.company;

import java.util.ArrayList;

public class Server {
    private ArrayList<User> users;
    private DialogLogic logic;

    public Server(){
        users = new ArrayList<>();
        logic = new DialogLogic();
    }

    public User createUser()
    {
        User client = new User(users.size());
        users.add(client);
        return client;
    }

    public String getStartMessage()
    {
        return AnswersStorage.startMessage + AnswersStorage.helpMessage;
    }

    public String replyToUser(User user, String messageFromUser)
    {
        return logic.getResponse(messageFromUser);
    }
}

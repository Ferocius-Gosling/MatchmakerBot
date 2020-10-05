package com.company;

import java.util.ArrayList;

public class Server {
    public static UserRepository users;
    private DialogLogic logic;

    public Server(){
        users = new UserRepository();
        logic = new DialogLogic();
    }

    public User createUser()
    {
        User client = new User(users.size());
        users.addUser(client);
        return client;
    }

    public String getStartMessage()
    {
        return AnswersStorage.startMessage + AnswersStorage.helpMessage;
    }

    public String replyToUser(User user, String messageFromUser)
    {
        return logic.getResponse(user, messageFromUser);
    }
}

package com.company;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot {
    public static UserRepository users;
    private DialogLogic logic;

    public Bot() {
        users = new UserRepository();
        logic = new DialogLogic();
    }

    public User createUser(long id) {
        User client = new User(id);
        users.addUser(client);
        return client;
    }

    public String getStartMessage() {
        return AnswersStorage.startMessage + AnswersStorage.helpMessage;
    }

    public String replyToUser(User user, String messageFromUser) {
        return logic.getResponse(user, messageFromUser);
    }
}

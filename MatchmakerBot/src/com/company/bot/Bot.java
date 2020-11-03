package com.company.bot;

import com.company.*;
import com.company.bot.inlineKeyboard.BotInlineKeyboardButton;
import com.company.bot.inlineKeyboard.InlineKeyboardData;

import java.util.ArrayList;

public class Bot {
    private UserRepository users;
    private DialogLogic logic;

    public Bot(UserRepository userRep, DialogLogic logic) {
        users = userRep;
        this.logic = logic;
    }

    public void createUser(long id) {
        User client = new User(id);
        users.addUser(client);
    }

    public Message replyToUser(long userId, String userName, Message messageFromUser) {
        if (users.getUser(userId) == null)
            createUser(userId);
        var user = users.getUser(userId);
        user.setUserName(userName);
        return generateMessage(user, messageFromUser);
    }

    public Message generateMessage(User user, Message messageFromUser) {
        return logic.getResponse(user, messageFromUser, users);

    }
}
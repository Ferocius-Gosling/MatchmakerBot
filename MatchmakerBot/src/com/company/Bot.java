package com.company;

import java.util.ArrayList;

public class Bot {
    public static UserRepository users;
    private DialogLogic logic;

    public Bot() {
        users = new UserRepository();
        logic = new DialogLogic();
    }

    public void createUser(long id) {
        User client = new User(id);
        users.addUser(client);
    }

    public Message replyToUser(long userId, String userName, Message messageFromUser) {
        if (users.getUser(userId) == null)
            createUser(userId);
        var user = Bot.users.getUser(userId);
        user.setUserName(userName);
        var generatedMessage = generateMessage(user, messageFromUser);
        
        if (user.getCurrentState() == DialogStates.FIND && messageFromUser.getTextMessage().equals("/find")) {
            generatedMessage.setInlineKeyboardData(new InlineKeyboardData());
            var inlinedKeyboardData = generatedMessage.getInlineKeyboardData();
            var rows = inlinedKeyboardData.getRows();
            rows.add(new ArrayList<BotInlineKeyboardButton>());
            rows.get(0).add(new BotInlineKeyboardButton("find\uD83D\uDC94", "/find"));
            rows.get(0).add(new BotInlineKeyboardButton("like❤", "/like"));
        }
        return generatedMessage;
    }

    public Message generateMessage(User user, Message messageFromUser) {
        return logic.getResponse(user, messageFromUser);

    }
}
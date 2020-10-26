package com.company;

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
        return generateMessage(user, messageFromUser);
    }

    public Message generateMessage(User user, Message messageFromUser) {
        return new Message(logic.getResponse(user, messageFromUser), user.getCurrentState());

    }
}
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

    public String replyToUser(long userId, String messageFromUser){
        if (users.getUser(userId) == null)
            createUser(userId);
        var user = Bot.users.getUser(userId);
        return generateMessage(user, messageFromUser);
    }

    public String generateMessage(User user, String messageFromUser) {

        return logic.getResponse(user, messageFromUser);
    }
}

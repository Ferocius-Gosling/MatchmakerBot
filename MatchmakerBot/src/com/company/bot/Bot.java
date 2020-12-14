package com.company.bot;

import com.company.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class Bot {
    private UserRepository users;
    private DialogLogic logic;

    public Bot(UserRepository userRep, DialogLogic logic) {
        users = userRep;
        this.logic = logic;
    }

    public void createUser(long id) throws SQLException, ClassNotFoundException {
        User client = new User(id);
        users.addUser(client);
    }

    public Message replyToUser(long userId, String userName, Message messageFromUser) throws SQLException,
            ClassNotFoundException, IOException, ParserConfigurationException, SAXException {
        if (users.getUser(userId) == null)
            createUser(userId);
        var user = users.getUser(userId);
        user.setUserName(userName);
        var message = generateMessage(user, messageFromUser);
        users.updateUserState(user);
        return message;
    }

    public Message generateMessage(User user, Message messageFromUser) throws SQLException, ClassNotFoundException,
            ParserConfigurationException, SAXException, IOException {
        return logic.getResponse(user, messageFromUser, users);

    }
}
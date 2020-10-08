package com.company;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Server extends TelegramLongPollingBot {
    public static UserRepository users;
    private DialogLogic logic;

    public Server() {
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

    @Override
    public void onUpdateReceived(Update update) {
        String messageFromUser = update.getMessage().getText();
        var userId = update.getMessage().getChatId();
        if (!users.getIds().contains(userId))
            createUser(update.getMessage().getChatId());
        var user = users.getUser(userId);
        var messageToSend = replyToUser(user, messageFromUser);
        sendMsg(userId.toString(), messageToSend);
    }

    public synchronized void sendMsg(String chatId, String messageToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Mathmaker_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}

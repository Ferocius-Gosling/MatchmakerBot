package com.company;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
    private Bot bot;

    public TelegramBot(Bot bot)
    {
        this.bot = bot;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageFromUser = update.getMessage().getText();
        var userId = update.getMessage().getChatId();
        if (!Bot.users.getIds().contains(userId))
            bot.createUser(update.getMessage().getChatId());
        var user = Bot.users.getUser(userId);
        var messageToSend = bot.replyToUser(user, messageFromUser);
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

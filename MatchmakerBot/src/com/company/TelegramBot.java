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
        try {
            sendMsg(userId.toString(), bot.replyToUser(userId, messageFromUser));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMsg(String chatId, String messageToSend) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToSend);
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return System.getenv("TelegramBotName");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TelegramBotToken");
    }
}

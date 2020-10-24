package com.company;

import org.glassfish.jersey.message.filtering.SelectableScopeResolver;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.desktop.SystemEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TelegramBot extends TelegramLongPollingBot {
    private Bot bot;

    public TelegramBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String textMessageFromUser = "/help";
            File photoFromUser = null;
            String photoId = null;
            var userId = update.getMessage().getChatId();
            var userName = update.getMessage().getFrom().getUserName();
            if (update.hasMessage() && update.getMessage().hasText())
                textMessageFromUser = update.getMessage().getText();
            if (update.getMessage().hasPhoto() && update.getMessage().getCaption() != null) {
                textMessageFromUser = update.getMessage().getCaption();
                var photos = update.getMessage().getPhoto();
                photoId = photos.get(photos.size() - 1).getFileId();
                GetFile getFile = new GetFile().setFileId(photoId);
                String filePath = execute(getFile).getFilePath();
                photoFromUser = this.downloadFile(filePath);
            }
            Message messageFromUser = new Message(photoFromUser, textMessageFromUser);
            Message messageToUser;
            if (userName != null)
                messageToUser = bot.replyToUser(userId, userName, messageFromUser);
            else
                messageToUser = new Message(AnswersStorage.noUsernameError);
            if (messageToUser.getPhoto() == null)
                sendMsg(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"));
            else
                sendPhoto(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"), messageToUser.getPhoto());
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

    public synchronized void sendPhoto(String chatId, String messageToSend, File photoToSend) throws TelegramApiException {
        SendPhoto sendPhoto = null;
        try {
            sendPhoto = new SendPhoto()
                    .setChatId(chatId)
                    .setPhoto("photo-name", new FileInputStream(photoToSend))
                    .setCaption(messageToSend);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        execute(sendPhoto);
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
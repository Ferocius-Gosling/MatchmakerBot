package com.company;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
            Long userId = 0L;
            String userName = "";

            if (update.hasCallbackQuery()) {
                userId = update.getCallbackQuery().getMessage().getChatId();
                userName = update.getCallbackQuery().getMessage().getFrom().getUserName();
                textMessageFromUser = update.getCallbackQuery().getData();
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                userId = update.getMessage().getChatId();
                userName = update.getMessage().getFrom().getUserName();
                textMessageFromUser = update.getMessage().getText();
            } else if (update.getMessage().hasPhoto() && update.getMessage().getCaption() != null) {
                userId = update.getMessage().getChatId();
                userName = update.getMessage().getFrom().getUserName();
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

            boolean isFinding = false;
            if (messageFromUser.getTextMessage().equals("/find") &&
                    messageToUser.getState() == DialogStates.FIND)
                isFinding = true;

            if (messageToUser.getPhoto() == null)
                sendMsg(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"), isFinding);
            else
                sendPhoto(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"), messageToUser.getPhoto(), isFinding);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMsg(String chatId, String messageToSend, boolean isFinding) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToSend);
        if (isFinding)
            sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        execute(sendMessage);
    }

    public synchronized void sendPhoto(String chatId, String messageToSend, File photoToSend, Boolean isFinding) throws TelegramApiException {
        SendPhoto sendPhoto = null;
        try {
            sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto("photo-name", new FileInputStream(photoToSend));
            sendPhoto.setCaption(messageToSend);
            if (isFinding)
                sendPhoto.setReplyMarkup(createInlineKeyboardMarkup());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        execute(sendPhoto);
    }


//    public synchronized void setKeyboard(SendMessage sendMessage) {
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        sendMessage.setReplyMarkup(replyKeyboardMarkup);
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(false);
//        List<KeyboardRow> keyboard = new ArrayList<>();
//        KeyboardRow keyboardFirstRow = new KeyboardRow();
//        KeyboardRow keyboardSecondRow = new KeyboardRow();
//        var b = new KeyboardButton();
//        keyboardFirstRow.add(new KeyboardButton("/help"));
//        keyboardFirstRow.add(new KeyboardButton("/reg"));
//        keyboardSecondRow.add(new KeyboardButton("/showbio"));
//        keyboardSecondRow.add(new KeyboardButton("/matches"));
//        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
//        replyKeyboardMarkup.setKeyboard(keyboard);
//    }

    public synchronized InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("find\uD83D\uDC94");
        inlineKeyboardButton1.setCallbackData("/find");
        inlineKeyboardButton2.setText("like❤");
        inlineKeyboardButton2.setCallbackData("/like");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
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
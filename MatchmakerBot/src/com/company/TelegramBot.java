package com.company;

import com.company.bot.*;
import com.company.bot.inlineKeyboard.BotInlineKeyboardButton;
import com.company.bot.inlineKeyboard.InlineKeyboardData;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(TelegramBot.class.getName());
    private Bot bot;

    public TelegramBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String textMessageFromUser = "";
            File photoFromUser = null;
            String photoId = null;
            Long userId = 0L;
            String userName = "";

            if (update.hasCallbackQuery()) {
                userId = update.getCallbackQuery().getMessage().getChatId();
                userName = update.getCallbackQuery().getFrom().getUserName();
                textMessageFromUser = update.getCallbackQuery().getData();
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                userId = update.getMessage().getChatId();
                userName = update.getMessage().getFrom().getUserName();
                textMessageFromUser = update.getMessage().getText();
            } else if (update.getMessage().hasPhoto()) {
                userId = update.getMessage().getChatId();
                userName = update.getMessage().getFrom().getUserName();
                if (update.getMessage().getCaption() != null)
                    textMessageFromUser = update.getMessage().getCaption();
                var photos = update.getMessage().getPhoto();
                photoId = photos.get(photos.size() - 1).getFileId();
                GetFile getFile = new GetFile().setFileId(photoId);
                String filePath = execute(getFile).getFilePath();
                photoFromUser = this.downloadFile(filePath);
            } else {
                userId = update.getMessage().getChatId();
                userName = update.getMessage().getFrom().getUserName();
                textMessageFromUser = "qwerty";
            }
            Message messageFromUser = new Message(photoFromUser, textMessageFromUser);
            Message messageToUser;
            if (userName != null)
                messageToUser = bot.replyToUser(userId, userName, messageFromUser);
            else
                messageToUser = new Message(AnswersStorage.noUsernameError);

            if (messageToUser.getPhoto() == null)
                sendMsg(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"), messageToUser.getInlineKeyboardData());
            else
                sendPhoto(userId.toString(), messageToUser.getTextMessage()
                        .replace("_", "\\_"), messageToUser.getPhoto(), messageToUser.getInlineKeyboardData());
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public synchronized void sendMsg(String chatId, String messageToSend, InlineKeyboardData inlineKeyboardData) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageToSend);
        if (inlineKeyboardData != null) {
            var inlineKeyboardMarkup = createInlineKeyboardMarkup(inlineKeyboardData);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        execute(sendMessage);
    }

    public synchronized void sendPhoto(String chatId, String messageToSend, File photoToSend, InlineKeyboardData inlineKeyboardData) throws TelegramApiException {
        SendPhoto sendPhoto = null;
        try {
            sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto("photo-name", new FileInputStream(photoToSend));
            sendPhoto.setCaption(messageToSend);
            if (inlineKeyboardData != null) {
                var inlineKeyboardMarkup = createInlineKeyboardMarkup(inlineKeyboardData);
                sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
            }
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

    public synchronized InlineKeyboardMarkup createInlineKeyboardMarkup(InlineKeyboardData inlineKeyboardData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < inlineKeyboardData.getRows().size(); i++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

            for (BotInlineKeyboardButton button : inlineKeyboardData.getRows().get(i)
            ) {
                var inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(button.getText());
                inlineKeyboardButton.setCallbackData(button.getCallbackData());
                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);

        }
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
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

import java.io.*;
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
                if (update.hasCallbackQuery()) {
                    var callbackQuery = update.getCallbackQuery();
                    userId = callbackQuery.getMessage().getChatId();
                    userName = callbackQuery.getFrom().getUserName();
                }
                if (update.hasMessage()) {
                    var message = update.getMessage();
                    userId = message.getChatId();
                    userName = message.getFrom().getUserName();
                }
                logUnhandledInput(update);
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
            logger.log(Level.WARNING, e.getMessage(), e);
            logUnhandledInput(update);
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

    public synchronized void sendPhoto(String chatId, String messageToSend, File photoToSend, InlineKeyboardData inlineKeyboardData) throws TelegramApiException, FileNotFoundException {
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
            logger.log(Level.WARNING, e.getMessage(), e);
            throw e;
        }
        execute(sendPhoto);
    }


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

    private void logUnhandledInput(Update update) {
        var userId = 0l;
        var userName = "";
        var hasMessage = update.hasMessage();
        var hasCallbackQuerry = update.hasCallbackQuery();
        var hasEditedMessage = update.hasEditedMessage();
        var text = "";
        var hasPhoto = false;
        var hasVideo = false;
        var hasLocation = false;
        var hasDocument = false;
        var hasAudio = false;
        var hasVoice = false;
        var hasSticker = false;
        var hasAnimation = false;
        var hasVideoNote = false;
        var caption = "";
        if (hasMessage) {
            var message = update.getMessage();
            userId = message.getChatId();
            userName = message.getFrom().getUserName();
            hasPhoto = message.hasPhoto();
            hasVideo = message.hasVideo();
            hasDocument = message.hasDocument();
            hasAudio = message.hasAudio();
            hasVoice = message.hasVoice();
            hasSticker = message.hasSticker();
            hasAnimation = message.hasAnimation();
            hasLocation = message.hasLocation();
            hasVideoNote = message.hasVideoNote();
            if (message.hasText())
                text = message.getText();
            if (message.getCaption() != null)
                caption = message.getCaption();
        }

        if (hasCallbackQuerry) {
            var callbackQuery = update.getCallbackQuery();
            userId = callbackQuery.getMessage().getChatId();
            userName = callbackQuery.getFrom().getUserName();
            hasPhoto = callbackQuery.getMessage().hasPhoto();
            hasVideo = callbackQuery.getMessage().hasVideo();
            hasDocument = callbackQuery.getMessage().hasDocument();
            hasAudio = callbackQuery.getMessage().hasAudio();
            hasVoice = callbackQuery.getMessage().hasVoice();
            hasSticker = callbackQuery.getMessage().hasSticker();
            hasAnimation = callbackQuery.getMessage().hasAnimation();
            hasLocation = callbackQuery.getMessage().hasLocation();
            hasVideoNote = callbackQuery.getMessage().hasVideoNote();
        }
        var a = update.getMessage().hasPhoto();
        logger.warning(String.format(
                "Unhandled situation: Unexpected data from Telegram;\n" +
                        "Id = %s;\n" +
                        "Username = %s;\n" +
                        "Has message = %s;\n" +
                        "Has callback query = %s;\n" +
                        "Has edited message = %s;\n" +
                        "Has photo = %s;\n" +
                        "Has video = %s;\n" +
                        "Has location = %s;\n" +
                        "Has document = %s;\n" +
                        "Has audio = %s;\n" +
                        "Has voice = %s;\n" +
                        "Has sticker = %s;\n" +
                        "Has animation = %s;\n" +
                        "Has videoNote = %s;\n" +
                        "Text = \"%s\";\n" +
                        "Caption = \"%s\"",
                userId, userName, hasMessage, hasCallbackQuerry, hasEditedMessage, hasPhoto,
                hasVideo, hasLocation, hasDocument, hasAudio, hasVoice, hasSticker,
                hasAnimation, hasVideoNote, text, caption));
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
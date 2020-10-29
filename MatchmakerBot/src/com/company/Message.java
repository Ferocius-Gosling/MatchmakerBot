package com.company;

import java.io.File;
import java.util.FormatFlagsConversionMismatchException;

public class Message {
    private File photo;
    private String textMessage;
    private DialogStates state = null;
    private InlineKeyboardData inlineKeyboardData = null;

    public Message(Message message, DialogStates state) {
        this.photo = message.getPhoto();
        this.textMessage = message.getTextMessage();
        this.state = state;
        this.inlineKeyboardData = null;
    }

    public Message(File photo, String textMessage) {
        this.photo = photo;
        this.textMessage = textMessage;
        state = null;
        this.inlineKeyboardData = null;
    }

    public Message(String textMessage) {
        this.photo = null;
        this.textMessage = textMessage;
        state = null;
        this.inlineKeyboardData = null;
    }

    public Message(File photo) {
        this.photo = photo;
        this.textMessage = "";
        this.inlineKeyboardData = null;
    }

    public File getPhoto() {
        return this.photo;
    }

    public void setInlineKeyboardData(InlineKeyboardData inlineKeyboardData) {
        this.inlineKeyboardData = inlineKeyboardData;
    }

    public InlineKeyboardData getInlineKeyboardData() {
        return inlineKeyboardData;
    }

    public DialogStates getState() {
        return this.state;
    }

    public String getTextMessage() {
        return this.textMessage;
    }
}

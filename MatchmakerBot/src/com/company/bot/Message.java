package com.company.bot;

import com.company.bot.inlineKeyboard.InlineKeyboardData;

import java.io.File;

public class Message {
    private File photo;
    private String textMessage;
    private InlineKeyboardData inlineKeyboardData;

    public Message(File photo, String textMessage) {
        this.photo = photo;
        this.textMessage = textMessage;
        this.inlineKeyboardData = null;
    }

    public Message(String textMessage) {
        this.photo = null;
        this.textMessage = textMessage;
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

    public String getTextMessage() {
        return this.textMessage;
    }
}

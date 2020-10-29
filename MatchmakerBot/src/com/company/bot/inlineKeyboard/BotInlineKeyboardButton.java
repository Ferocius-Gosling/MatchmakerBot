package com.company.bot.inlineKeyboard;

public class BotInlineKeyboardButton {
    private String text;
    private String callbackData;

    public BotInlineKeyboardButton(String text, String callbackData) {
        this.text = text;
        this.callbackData = callbackData;
    }

    public String getText() {
        return text;
    }

    public String getCallbackData() {
        return callbackData;
    }
}

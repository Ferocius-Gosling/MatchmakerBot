package com.company.bot.inlineKeyboard;

import com.company.bot.inlineKeyboard.BotInlineKeyboardButton;

import java.util.ArrayList;

public class InlineKeyboardData {
    private ArrayList<ArrayList<BotInlineKeyboardButton>> rows;

    public InlineKeyboardData() {
        rows = new ArrayList<>();
    }

    public void addButton(int rowNumber, BotInlineKeyboardButton button) {
        rows.get(rowNumber).add(button);
    }

    public void addRow() {
        rows.add(new ArrayList<>());
    }

    public ArrayList<ArrayList<BotInlineKeyboardButton>> getRows() {
        return rows;
    }
}

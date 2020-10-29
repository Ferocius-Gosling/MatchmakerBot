package com.company;

import java.util.ArrayList;

public class InlineKeyboardData {
    private ArrayList<ArrayList<BotInlineKeyboardButton>> rows;

    public InlineKeyboardData() {
        rows = new ArrayList<>();
    }

    public void addButton(int rowNumber, BotInlineKeyboardButton button) {
        rows.get(rowNumber).add(button);
    }

    public ArrayList<ArrayList<BotInlineKeyboardButton>> getRows() {
        return rows;
    }
}

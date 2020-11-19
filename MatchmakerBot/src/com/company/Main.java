package com.company;

import com.company.bot.Bot;
import com.company.bot.DialogLogic;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        DialogLogic logic = new DialogLogic();
        UserRepository users = new UserRepository();
        try { // todo watch on catch, refactor
            users.loadUsers();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Bot bot = new Bot(users, logic);
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramBot(bot));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}

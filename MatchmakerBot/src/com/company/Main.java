package com.company;

import com.company.bot.Bot;
import com.company.bot.DialogLogic;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(TelegramBot.class.getName());

    public static void main(String[] args) throws IOException {
        configureLogger();
        DialogLogic logic = new DialogLogic();
        UserRepository users = new UserRepository();
//        try {
//            users.loadUsers();
//        } catch (Exception e) {
//            logger.log(Level.WARNING, e.getMessage(), e);
//        }
        Bot bot = new Bot(users, logic);
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramBot(bot));
        } catch (TelegramApiRequestException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

    }

    private static void configureLogger() throws IOException {
        StringJoiner joiner = new StringJoiner(File.separator)
                .add("MatchmakerBot")
                .add("src")
                .add("com")
                .add("company")
                .add("log.config");
        var pathToConfig = File.separator + joiner.toString();
        String currentDir = System.getProperty("user.dir");
        LogManager.getLogManager().readConfiguration(
                new FileInputStream(
                        currentDir + pathToConfig));
    }
}

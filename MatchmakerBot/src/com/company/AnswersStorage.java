package com.company;

public class AnswersStorage {
    public static String helpMessage =
            "Напишите /reg, чтобы зарегистрировать себя в базе. \n" +
            "Напишите /help, чтобы вывести это сообщение ещё раз";

    public static String defaultMessage = "Не понимаю вашего запроса." +
            " Вызовите /help для справки.";
    private static String botName = "MatchmakerBot - Вячесlove";
    public static String registerMessage = "Регистрация: Напишите своё имя.";
    public static String startMessage = "Приветствую странник. Меня зовут {}".format(botName);
}

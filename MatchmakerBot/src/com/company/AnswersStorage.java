package com.company;

public class AnswersStorage {
    public static String helpMessage =
            "Напишите /reg, чтобы зарегистрировать себя в базе.\n" +
                    "Напишите /help, чтобы вывести это сообщение ещё раз.\n" +
                    "Напишите /showbio, чтобы посмотреть свою анкету.\n" +
                    "Напишите /match, чтобы посмотреть другие анкеты.\n";

    public static String defaultMessage = "Не понимаю вашего запроса." +
            " Вызовите /help для справки.";
    private final static String botName = "MatchmakerBot - Вячесlove";
    public static String registerNameMessage = "Регистрация: Напишите своё имя.";
    public static String regAgeMessage = "Напишите свой возраст арабскими цифрами, в пределах от 0 до 150 лет.";
    public static String wrongAgeMessage = "Я же попросил написать возраст арабскими цифрами, в пределах от 0 до 150 лет.!\n" +
            "Перепишите пожалуйста.";
    public static String regCityMessage = "Напишите город в котором вы находитесь.";
    public static String regInfoMessage = "Расскажите о себе одним сообщением.";
    public static String startMessage = String.format("Приветствую странник. Меня зовут %s!\n", botName);
    public static String forcedRegMessage = "Сейчас начнётся процесс регистрации. \n\n";
    public static String showbioErrorMessage = "Вы ещё не зарегистрировались. \n" +
            "Я не могу показать вам вашу анкету.\n" + forcedRegMessage;
    public static String matchErrorMessage = "Вы ещё не зарегистрировались. \n" +
            "Я не могу показать вам другие анкеты.\n" + forcedRegMessage;
    public static String regErrorMessage = "Вы уже зарегистрированы.";

    public static String getUserInfo(User user) {
        return String.format("Имя: %s\nВозраст: %d\nГород: %s\n++++Описание++++\n%s",
                user.getName(), user.getAge(), user.getCity(), user.getInfo());
    }

}

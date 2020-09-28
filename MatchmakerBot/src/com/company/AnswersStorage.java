package com.company;

public class AnswersStorage {
    public static String helpMessage =
            "Напишите /reg, чтобы зарегистрировать себя в базе. \n" +
                    "Напишите /help, чтобы вывести это сообщение ещё раз";

    public static String defaultMessage = "Не понимаю вашего запроса." +
            " Вызовите /help для справки.";
    private final static String botName = "MatchmakerBot - Вячесlove";
    public static String registerNameMessage = "Регистрация: Напишите своё имя.";
    public static String regAgeMessage = "Напишите свой возраст арабскими цифрами.";
    public static String wrongAgeMessage = "Я же попросил написать возраст арабскими цифрами!\n" +
            "Перепишите пожалуйста.";
    public static String regCityMessage = "Напишите город в котором вы находитесь.";
    public static String regInfoMessage = "Расскажите о себе одним сообщением.";
    public static String regSuccesfull = "Регистрация завершена поздравляю.";
    public static String startMessage = String.format("Приветствую странник. Меня зовут %s!\n",botName);
    public static String showbioErrorMessage = "Вы ещё не зарегистрировались. \n" +
            "Я не могу показать вам вашу анкету.";
    public static String regErrorMessage = "Вы уже зарегистрированы.";

}

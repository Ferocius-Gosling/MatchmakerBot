package com.company;

public class DialogLogic {
    public String getResponse(String mes) {
        return switch (mes) {
            case "/help":
                yield getHelp();
            case "/reg":
                yield registerUser();
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String getHelp() {
        return AnswersStorage.helpMessage;
    }

    private String registerUser() {
        return AnswersStorage.registerMessage;
    }
}

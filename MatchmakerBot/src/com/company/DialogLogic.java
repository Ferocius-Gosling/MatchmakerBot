package com.company;

public class DialogLogic {
    public String getResponse(User user, String mes) {
        if (user.isReg())
            return registerUser(user, mes);
        return switch (mes) {
            case "/help":
                yield AnswersStorage.helpMessage;
            case "/reg":
                yield registerUser(user);
            case "/match":
                yield match(user);
            case "/showbio":
                yield showBio(user);
            case "/start":
                yield AnswersStorage.startMessage + AnswersStorage.helpMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String match(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield AnswersStorage.matchErrorMessage;
            case MENU:
                yield AnswersStorage.getUserInfo(Bot.users.getNextUser());
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String registerUser(User user, String mes) {
        return switch (user.getCurrentState()) {
            case REG_NAME:
                user.setName(mes);
                user.changeCurrentState(DialogStates.REG_AGE);
                yield AnswersStorage.regAgeMessage;
            case REG_AGE:
                yield regAge(user, mes);
            case REG_CITY:
                user.setCity(mes);
                user.changeCurrentState(DialogStates.REG_INFO);
                yield AnswersStorage.regInfoMessage;
            case REG_INFO:
                user.setInfo(mes);
                user.setReg(false);
                user.changeCurrentState(DialogStates.MENU);
                yield AnswersStorage.getUserInfo(user);
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String showBio(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield AnswersStorage.showbioErrorMessage;
            case MENU:
                yield AnswersStorage.getUserInfo(user);
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String regAge(User user, String sAge) {
        var iAge = 0;
        try {
            iAge = Integer.parseInt(sAge);
        } catch (NumberFormatException nfe) {
            return AnswersStorage.wrongAgeMessage;
        }
        ;
        if ((iAge < 0) || (iAge > 150))
            return AnswersStorage.wrongAgeMessage;
        user.setAge(iAge);
        user.changeCurrentState(DialogStates.REG_CITY);
        return AnswersStorage.regCityMessage;
    }

    private String registerUser(User user) {
        return switch (user.getCurrentState()) {
            case START:
                user.changeCurrentState(DialogStates.REG_NAME);
                user.setReg(true);
                yield AnswersStorage.registerNameMessage;
            case MENU:
                yield AnswersStorage.regErrorMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };

    }
}

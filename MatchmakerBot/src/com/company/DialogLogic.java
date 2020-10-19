package com.company;

public class DialogLogic {
    private User userInQuestion;

    public String getResponse(User user, String mes) {
        if (!user.isRegistred()) //todo not
            return registerUser(user, mes);
        return switch (mes) {
            case "/help":
                yield AnswersStorage.helpMessage;
            case "/reg":
                yield registerUser(user);
            case "/find":
                yield find(user);
            case "/like":
                yield like(user);
            case "/showbio":
                yield showBio(user);
            case "/matches":
                yield showMatches(user);
            case "/stop":
                yield stop(user);
            case "/start":
                yield AnswersStorage.startMessage + AnswersStorage.helpMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String stop(User user){
        return switch (user.getCurrentState()) {
            case FIND:
                user.changeCurrentState(DialogStates.MENU);
                yield AnswersStorage.stopMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String showMatches(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield AnswersStorage.matchErrorMessage + registerUser(user);
            case MENU:
            case FIND:
                var replyBuilder = new StringBuilder();
                replyBuilder.append(AnswersStorage.showMatchesMessage);
                for (User u: user.getMatchedUsers()){
                    replyBuilder.append(AnswersStorage.getUserInfo(u));
                    replyBuilder.append("\n\n");
                }
                yield replyBuilder.toString();
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String like(User user){
        return switch (user.getCurrentState()) {
            case FIND:
                user.addToWhoLikes(userInQuestion);
                yield AnswersStorage.likeMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String find(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield AnswersStorage.matchErrorMessage + registerUser(user);
            case MENU:
            case FIND:
                user.changeCurrentState(DialogStates.FIND);
                userInQuestion = Bot.users.getNextUser();
                yield AnswersStorage.getUserInfo(userInQuestion);
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
                user.setReg(true);
                user.changeCurrentState(DialogStates.MENU);
                yield AnswersStorage.getUserInfo(user);
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String showBio(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield AnswersStorage.showbioErrorMessage + registerUser(user);
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
                user.setReg(false);
                yield AnswersStorage.registerNameMessage;
            case MENU:
                yield AnswersStorage.regErrorMessage;
            default:
                yield AnswersStorage.defaultMessage;
        };

    }
}

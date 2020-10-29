package com.company.bot;

public class DialogLogic {
    public Message getResponse(User user, Message messageFromUser) {
        if (!user.isRegistered()) //todo not
            return registerUser(user, messageFromUser);
        return switch (messageFromUser.getTextMessage()) {
            case "/help":
                yield new Message(AnswersStorage.helpMessage);
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
                yield new Message(AnswersStorage.startMessage + AnswersStorage.helpMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message stop(User user) {
        return switch (user.getCurrentState()) {
            case FIND:
                user.changeCurrentState(DialogState.MENU);
                yield new Message(AnswersStorage.stopMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message showMatches(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.matchErrorMessage + registerUser(user).getTextMessage());
            case MENU:
            case FIND:
                var replyBuilder = new StringBuilder();
                replyBuilder.append(AnswersStorage.showMatchesMessage);
                for (User u : user.getMatchedUsers()) {
                    replyBuilder.append(AnswersStorage.getUserInfo(u));
                    replyBuilder.append("\n\n");
                    replyBuilder.append("@");
                    replyBuilder.append(u.getUserName());
                    replyBuilder.append("\n\n");
                }
                user.clearMatched();
                yield new Message(replyBuilder.toString());
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message like(User user) {
        return switch (user.getCurrentState()) {
            case FIND:
                user.addToWhoLikes();
                yield new Message(AnswersStorage.likeMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message find(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.matchErrorMessage + registerUser(user).getTextMessage());
            case MENU:
            case FIND:
                user.changeCurrentState(DialogState.FIND);
                var userInQuestion = Bot.users.getNextUser(user);
                if (userInQuestion == null) yield new Message(AnswersStorage.nobodyElseMessage);
                user.setUserInQuestion(userInQuestion);
                yield new Message(userInQuestion.getUserPhoto(), (AnswersStorage.getUserInfo(userInQuestion)
                + AnswersStorage.forwardMessage));
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message registerUser(User user, Message message) {
        return switch (user.getCurrentState()) {
            case REG_NAME:
                user.setName(message.getTextMessage());
                user.changeCurrentState(DialogState.REG_AGE);
                yield new Message(AnswersStorage.regAgeMessage);
            case REG_AGE:
                yield regAge(user, message.getTextMessage());
            case REG_CITY:
                user.setCity(message.getTextMessage());
                user.changeCurrentState(DialogState.REG_INFO);
                yield new Message(AnswersStorage.regInfoMessage);
            case REG_INFO:
                user.setInfo(message.getTextMessage());
                user.setUserPhoto(message.getPhoto());
                user.setReg(true);
                user.changeCurrentState(DialogState.MENU);
                yield new Message(user.getUserPhoto(),
                        AnswersStorage.getUserInfo(user) + AnswersStorage.startFindingMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message showBio(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.showbioErrorMessage + registerUser(user).getTextMessage());
            case MENU:
                yield new Message(user.getUserPhoto(),
                        AnswersStorage.getUserInfo(user) + AnswersStorage.forwardMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };
    }

    private Message regAge(User user, String sAge) {
        var iAge = 0;
        try {
            iAge = Integer.parseInt(sAge);
        } catch (NumberFormatException nfe) {
            return new Message(AnswersStorage.wrongAgeMessage);
        }
        if ((iAge < 0) || (iAge > 150))
            return new Message(AnswersStorage.wrongAgeMessage);
        user.setAge(iAge);
        user.changeCurrentState(DialogState.REG_CITY);
        return new Message(AnswersStorage.regCityMessage);
    }

    private Message registerUser(User user) {
        return switch (user.getCurrentState()) {
            case MENU:
            case FIND:
            case START:
                user.changeCurrentState(DialogState.REG_NAME);
                user.setReg(false);
                yield new Message(AnswersStorage.registerNameMessage);
            default:
                yield new Message(AnswersStorage.defaultMessage);
        };

    }
}
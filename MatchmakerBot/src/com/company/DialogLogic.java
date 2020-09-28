package com.company;

public class DialogLogic {
    public String getResponse(User user, String mes) {
        return switch (user.getCurrentState()) {
            case START:
                yield switch (mes){
                    case "/help":
                        yield getHelp();
                    case "/reg":
                        user.changeCurrentState(States.REG_NAME);
                        yield registerUser();
                    default:
                        yield AnswersStorage.defaultMessage;
                };
            case REG_NAME:
                user.setName(mes);
                user.changeCurrentState(States.REG_AGE);
                yield AnswersStorage.regAgeMessage;
            case REG_AGE:
                var iAge = 0;
                try {
                    iAge = Integer.parseInt(mes);
                } catch (NumberFormatException nfe)
                {
                    yield AnswersStorage.wrongAgeMessage;
                };
                user.setAge(iAge);
                user.changeCurrentState(States.REG_CITY);
                yield AnswersStorage.regCityMessage;
            case REG_CITY:
                user.setCity(mes);
                user.changeCurrentState(States.REG_INFO);
                yield AnswersStorage.regInfoMessage;
            case REG_INFO:
                user.setInfo(mes);
                user.changeCurrentState(States.MENU);
                yield AnswersStorage.regSuccesfull;
            case MENU:
                yield switch (mes){
                    case "/help":
                        yield getHelp();
                    case "/reg":
                        yield AnswersStorage.regErrorMessage;
                    default:
                        yield AnswersStorage.defaultMessage;
                };
            default:
                yield AnswersStorage.defaultMessage;
        };
    }

    private String getHelp() {
        return AnswersStorage.helpMessage;
    }

    private String registerUser() {
        return AnswersStorage.registerNameMessage;
    }
}

package com.company.bot;

import com.company.UserRepository;
import com.company.bot.inlineKeyboard.BotInlineKeyboardButton;
import com.company.bot.inlineKeyboard.InlineKeyboardData;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DialogLogic {
    private static final Logger logger = Logger.getLogger(DialogLogic.class.getName());

    public Message getResponse(User user, Message messageFromUser, UserRepository users)
            throws SQLException, IOException, SAXException, ParserConfigurationException {
        AnswersStorage.configureAnswerStorage(user.getLang());
        if (!user.isRegistered())
            return registerUser(user, messageFromUser, users);
        return switch (messageFromUser.getTextMessage()) {
            case "/help":
                yield new Message(AnswersStorage.getHelpMessage());
            case "/reg":
                yield registerUser(user);
            case "/find":
                yield find(user, users);
            case "/like":
                yield like(user, users);
            case "/showbio":
                yield showBio(user);
            case "/matches":
                yield showMatches(user, users);
            case "/stop":
                yield stop(user);
            case "/start":
                yield new Message(AnswersStorage.getStartMessage() + AnswersStorage.getHelpMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message stop(User user) {
        return switch (user.getCurrentState()) {
            case FIND:
                user.changeCurrentState(DialogState.MENU);
                yield new Message(AnswersStorage.getStopMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message showMatches(User user, UserRepository users) throws SQLException {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.getMatchErrorMessage() + registerUser(user).getTextMessage());
            case MENU:
            case FIND:
                var replyBuilder = new StringBuilder();
                replyBuilder.append(AnswersStorage.getShowMatchesMessage());
                for (User u : user.getMatchedUsers()) {
                    replyBuilder.append(AnswersStorage.getUserInfo(u));
                    replyBuilder.append("\n");
                    replyBuilder.append("@");
                    replyBuilder.append(u.getUserName());
                    replyBuilder.append("\n");
                }
                user.clearMatched();
                users.clearMatches(user);
                yield new Message(replyBuilder.toString());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message like(User user, UserRepository users) throws SQLException {
        return switch (user.getCurrentState()) {
            case FIND:
                user.addToWhoLikes(users);
                yield new Message(AnswersStorage.getLikeMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message find(User user, UserRepository users) throws SQLException {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.getMatchErrorMessage() + registerUser(user).getTextMessage());
            case MENU:
            case FIND:
                user.changeCurrentState(DialogState.FIND);
                var userInQuestion = users.getNextUser(user);
                if (userInQuestion == null) yield new Message(AnswersStorage.getNobodyElseMessage());
                user.setUserInQuestion(userInQuestion);
                users.updateUserLastFind(userInQuestion);
                var generatedMessage = new Message(userInQuestion.getUserPhoto(), (AnswersStorage.getUserInfo(userInQuestion)
                        + AnswersStorage.getForwardMessage()));
                var inlineKeyboardData = new InlineKeyboardData();
                inlineKeyboardData.addRow();
                inlineKeyboardData.addButton(0, new BotInlineKeyboardButton("find\uD83D\uDC94", "/find"));
                inlineKeyboardData.addButton(0, new BotInlineKeyboardButton("like❤", "/like"));
                generatedMessage.setInlineKeyboardData(inlineKeyboardData);
                yield generatedMessage;
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message registerUser(User user, Message message, UserRepository users) {
        return switch (user.getCurrentState()) {
            case REG_NAME:
                user.setName(message.getTextMessage());
                user.changeCurrentState(DialogState.REG_AGE);
                yield new Message(AnswersStorage.getRegAgeMessage());
            case REG_AGE:
                yield regAge(user, message.getTextMessage());
            case REG_CITY:
                user.setCity(message.getTextMessage());
                user.changeCurrentState(DialogState.REG_INFO);
                yield new Message(AnswersStorage.getRegInfoMessage());
            case REG_INFO:
                user.setInfo(message.getTextMessage());
                user.setUserPhoto(message.getPhoto());
                user.setReg(true);
                user.changeCurrentState(DialogState.MENU);
                logger.info(String.format("Registration: User: %s, UserID: %s, Name: %s",
                        user.getUserName(), user.getId(), user.getName()));
                yield new Message(user.getUserPhoto(),
                        AnswersStorage.getUserInfo(user) + AnswersStorage.getStartFindingMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message showBio(User user) {
        return switch (user.getCurrentState()) {
            case START:
                yield new Message(AnswersStorage.getShowbioErrorMessage() + registerUser(user).getTextMessage());
            case MENU:
            case FIND:
                yield new Message(user.getUserPhoto(),
                        AnswersStorage.getUserInfo(user) + AnswersStorage.getForwardMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };
    }

    private Message regAge(User user, String sAge) {
        var iAge = 0;
        try {
            iAge = Integer.parseInt(sAge);
        } catch (NumberFormatException nfe) {
            return new Message(AnswersStorage.getWrongAgeMessage());
        }
        if ((iAge < 0) || (iAge > 150))
            return new Message(AnswersStorage.getWrongAgeMessage());
        user.setAge(iAge);
        user.changeCurrentState(DialogState.REG_CITY);
        return new Message(AnswersStorage.getRegCityMessage());
    }

    private Message registerUser(User user) {
        return switch (user.getCurrentState()) {
            case MENU:
            case FIND:
            case START:
                user.changeCurrentState(DialogState.REG_NAME);
                user.setReg(false);
                yield new Message(AnswersStorage.getRegisterNameMessage());
            default:
                yield new Message(AnswersStorage.getDefaultMessage());
        };

    }
}
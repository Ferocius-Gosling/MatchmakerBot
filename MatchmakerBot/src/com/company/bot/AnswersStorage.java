package com.company.bot;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;

public class AnswersStorage {

    private DocumentBuilderFactory docBuilderFactory;
    private DocumentBuilder docBuilder;
    private static Document answers;

    public AnswersStorage() throws ParserConfigurationException, IOException, SAXException {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        answers = docBuilder.parse(new File(configureAnswersPath("ru.xml")));
    }

    private String configureAnswersPath(String fileName) {
        StringJoiner joiner = new StringJoiner(File.separator)
                .add("MatchmakerBot")
                .add("src")
                .add("com")
                .add("company")
                .add("bot")
                .add("answers")
                .add(fileName);
        var pathToDoc = File.separator + joiner.toString();
        String currentDir = System.getProperty("user.dir");
        return currentDir + pathToDoc;
    }

    public static String helpMessage = answers.getElementsByTagName("helpMessage").item(0).getNodeValue();


    public static String defaultMessage = answers.getElementsByTagName("defaultMessage").item(0).getNodeValue();
    private final static String botName = answers.getElementsByTagName("botName").item(0).getNodeValue();
    public static String registerNameMessage = answers.getElementsByTagName("registerNameMessage").item(0).getNodeValue();
    public static String regAgeMessage = answers.getElementsByTagName("regAgeMessage").item(0).getNodeValue();
    public static String wrongAgeMessage = answers.getElementsByTagName("wrongAgeMessage").item(0).getNodeValue();
    public static String regCityMessage = answers.getElementsByTagName("regCityMessage").item(0).getNodeValue();
    public static String regInfoMessage = answers.getElementsByTagName("regInfoMessage").item(0).getNodeValue();
    public static String startMessage = String.format(answers.getElementsByTagName("startMessage").item(0).getNodeValue() +
            "\n", botName);
    public static String forcedRegMessage = answers.getElementsByTagName("forcedRegMessage").item(0).getNodeValue() + "\n\n";
    public static String showbioErrorMessage = answers.getElementsByTagName("showbioErrorMessage").item(0).getNodeValue()
            + forcedRegMessage;
    public static String matchErrorMessage = defaultMessage = answers.getElementsByTagName("matchErrorMessage").item(0).getNodeValue();
    public static String noUsernameError = answers.getElementsByTagName("noUsernameError").item(0).getNodeValue();
    public static String showMatchesMessage = answers.getElementsByTagName("showMatchesMessage").item(0).getNodeValue() + "\n\n";
    public static String likeMessage = answers.getElementsByTagName("likeMessage").item(0).getNodeValue();
    public static String stopMessage = answers.getElementsByTagName("stopMessage").item(0).getNodeValue();
    public static String nobodyElseMessage = answers.getElementsByTagName("nobodyElseMessage").item(0).getNodeValue();
    public static String forwardMessage = "\n" + answers.getElementsByTagName("forwardMessage").item(0).getNodeValue() + "\n";
    public static String startFindingMessage = "\n"+answers.getElementsByTagName("nobodyElseMessage").item(0).getNodeValue();

    public static String getUserInfo(User user) {
        return String.format(answers.getElementsByTagName("UserInfo").item(0).getNodeValue(),
                user.getName(), user.getAge(), user.getCity(), user.getInfo());
    }

}
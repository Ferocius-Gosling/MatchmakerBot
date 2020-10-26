package com.company;

import java.io.File;
import java.util.FormatFlagsConversionMismatchException;

public class Message {
    private File photo;
    private String textMessage;
    private DialogStates state = null;

    public Message(Message message, DialogStates state) {
        this.photo = message.getPhoto();
        this.textMessage = message.getTextMessage();
        this.state = state;
    }

    public Message(File photo, String textMessage) {
        this.photo = photo;
        this.textMessage = textMessage;
        state = null;
    }

    public Message(String textMessage) {
        this.photo = null;
        this.textMessage = textMessage;
        state = null;
    }

    public Message(File photo) {
        this.photo = photo;
        this.textMessage = "";
    }

    public File getPhoto() {
        return this.photo;
    }

    public DialogStates getState() {
        return this.state;
    }

    public String getTextMessage() {
        return this.textMessage;
    }
}

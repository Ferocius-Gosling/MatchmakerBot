package com.company;

import java.io.File;
import java.util.FormatFlagsConversionMismatchException;

public class Message {
    private File photo;
    private String textMessage;

    public Message(File photo, String textMessage) {
        this.photo = photo;
        this.textMessage = textMessage;
    }

    public Message(String textMessage) {
        this.photo = null;
        this.textMessage = textMessage;
    }

    public Message(File photo) {
        this.photo = photo;
        this.textMessage = "";
    }

    public File getPhoto(){return this.photo;}
    public String getTextMessage(){return this.textMessage;}
}

package models;

import java.text.DateFormat;
import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageReceiver;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser,String messageReceiver) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageReceiver=messageReceiver;
        // Initialize to current time
        messageTime = new Date().getTime();
    }
    public ChatMessage(String messageText, String messageUser,long messageTime,String messageReceiver) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime= messageTime;
        this.messageReceiver=messageReceiver;

    }
    public ChatMessage(){

    }

    public String getMessageReceiver() {
        return messageReceiver;
    }

    public void setMessageReceiver(String messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}

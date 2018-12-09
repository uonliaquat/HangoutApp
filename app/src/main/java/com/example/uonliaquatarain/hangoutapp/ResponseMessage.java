package com.example.uonliaquatarain.hangoutapp;

public class ResponseMessage {

    String textMessage;
    Boolean isMe;

    public ResponseMessage(String textMessage, Boolean isMe) {
        this.textMessage = textMessage;
        this.isMe = isMe;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public Boolean getMe() {
        return isMe;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setMe(Boolean me) {
        isMe = me;
    }
}

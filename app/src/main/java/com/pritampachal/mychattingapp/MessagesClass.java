package com.pritampachal.mychattingapp;

public class MessagesClass {
    private String message,senderId,currentTime;

    public MessagesClass() {
    }

    public MessagesClass(String message, String senderId, String currentTime) {
        this.message = message;
        this.senderId = senderId;
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}

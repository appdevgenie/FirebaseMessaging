package com.appdevgenie.firebasemessaging.Models;

public class FirebaseCloudMessage {

    private String to;
    private MessageData messageData;

    public FirebaseCloudMessage() {
    }

    public FirebaseCloudMessage(String to, MessageData messageData) {
        this.to = to;
        this.messageData = messageData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MessageData getMessageData() {
        return messageData;
    }

    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

    @Override
    public String toString() {
        return "FirebaseCloudMessage{" +
                "to='" + to + '\'' +
                ", messageData=" + messageData +
                '}';
    }
}

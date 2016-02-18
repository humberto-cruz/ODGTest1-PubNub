package com.example.prefixa_01.odgtest1.adt;

/**
 * Created by Humberto on 18/02/2016.
 */
public class ChatMessage {

    private String sender;
    private String message;
    private long timeStamp;

    public ChatMessage(String sender, String message, long timeStamp){
        this.sender = sender;
        this.message = message;
        this.timeStamp=timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString(){
        return this.message;
    }

    @Override
    public int hashCode() {
        return (this.sender + this.message + this.timeStamp).hashCode();
    }
}

package com.example.android.chatty;

/**
 * Created by Soul on 10/11/2017.
 */

public class SingleMessage  {

    private String messages , type , from;
    private long time;
    private boolean seen;

    public SingleMessage(String messages, boolean seen, long time, String type , String from) {
        this.messages = messages;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;

    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public SingleMessage(){

    }


}

package com.dusanjovanov.meetups3.models;

/**
 * Created by duca on 9/1/2017.
 */

public class ChatMessage {
    private String displayName;
    private String photoUrl;
    private String message;
    private long time;

    public ChatMessage() {
    }

    public ChatMessage(String displayName, String photoUrl,String message, long time) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.message = message;
        this.time = time;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

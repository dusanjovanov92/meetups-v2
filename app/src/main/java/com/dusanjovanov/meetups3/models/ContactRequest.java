package com.dusanjovanov.meetups3.models;

/**
 * Created by duca on 6/1/2017.
 */

public class ContactRequest {
    private int id;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
